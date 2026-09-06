package com.vedant.jobtracker.service;

import com.vedant.jobtracker.dto.AnalysisResult;
import com.vedant.jobtracker.dto.JobApplicationRequest;
import com.vedant.jobtracker.dto.JobApplicationResponse;
import com.vedant.jobtracker.entity.ApplicationStatus;
import com.vedant.jobtracker.entity.JobApplication;
import com.vedant.jobtracker.entity.User;
import com.vedant.jobtracker.exception.ResourceNotFoundException;
import com.vedant.jobtracker.repository.JobApplicationRepository;
import com.vedant.jobtracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final AiAnalysisService aiAnalysisService;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository,
                                 UserRepository userRepository,
                                 AiAnalysisService aiAnalysisService) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    public List<JobApplicationResponse> getAll(String email) {
        User user = getUser(email);
        return jobApplicationRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public JobApplicationResponse getOne(Long id, String email) {
        return toResponse(getOwnedApplication(id, email));
    }

    public JobApplicationResponse create(JobApplicationRequest request, String email) {
        User user = getUser(email);

        JobApplication app = new JobApplication();
        app.setCompany(request.getCompany());
        app.setJobTitle(request.getJobTitle());
        app.setAppliedDate(request.getAppliedDate());
        app.setNotes(request.getNotes());
        app.setStatus(request.getStatus() != null ? request.getStatus() : ApplicationStatus.APPLIED);
        app.setUser(user);

        return toResponse(jobApplicationRepository.save(app));
    }

    public JobApplicationResponse update(Long id, JobApplicationRequest request, String email) {
        JobApplication app = getOwnedApplication(id, email);

        app.setCompany(request.getCompany());
        app.setJobTitle(request.getJobTitle());
        app.setAppliedDate(request.getAppliedDate());
        app.setNotes(request.getNotes());
        if (request.getStatus() != null) {
            app.setStatus(request.getStatus());
        }

        return toResponse(jobApplicationRepository.save(app));
    }

    public void delete(Long id, String email) {
        jobApplicationRepository.delete(getOwnedApplication(id, email));
    }

    // Orchestrates the AI analysis: checks preconditions, delegates the actual
    // AI call to AiAnalysisService, then saves the results onto this application.
    public JobApplicationResponse analyze(Long id, String jobDescription, String email) {
        if (jobDescription == null || jobDescription.isBlank()) {
            throw new IllegalArgumentException("Paste a job description first.");
        }

        User user = getUser(email);
        if (user.getResumeText() == null) {
            throw new IllegalArgumentException("Upload a resume before running an analysis.");
        }

        JobApplication app = getOwnedApplication(id, email);

        AnalysisResult result = aiAnalysisService.analyze(user.getResumeText(), jobDescription);

        app.setJobDescription(jobDescription);
        app.setMatchScore(result.matchScore());
        app.setMissingSkills(String.join(",", result.missingSkills()));
        app.setResumeSuggestions(result.suggestions());
        app.setAnalyzedAt(LocalDateTime.now());

        return toResponse(jobApplicationRepository.save(app));
    }

    // Fetches the application AND checks it belongs to the requesting user.
    // Both failure cases return the same "not found" error, so someone
    // probing other people's IDs can't tell "doesn't exist" apart from
    // "exists, but isn't yours."
    // Package-private (not private) so InterviewQuestionService can reuse this
    // exact ownership check instead of duplicating it.
    JobApplication getOwnedApplication(Long id, String email) {
        User user = getUser(email);
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Application not found");
        }
        return app;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private JobApplicationResponse toResponse(JobApplication app) {
        List<String> missingSkills = (app.getMissingSkills() == null || app.getMissingSkills().isBlank())
                ? List.of()
                : Arrays.asList(app.getMissingSkills().split(","));

        return new JobApplicationResponse(
                app.getId(),
                app.getCompany(),
                app.getJobTitle(),
                app.getStatus(),
                app.getAppliedDate(),
                app.getNotes(),
                app.getCreatedAt(),
                app.getJobDescription(),
                app.getMatchScore(),
                missingSkills,
                app.getResumeSuggestions(),
                app.getAnalyzedAt()
        );
    }
}

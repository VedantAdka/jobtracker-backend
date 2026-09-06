package com.vedant.jobtracker.service;

import com.vedant.jobtracker.dto.InterviewQuestionResponse;
import com.vedant.jobtracker.entity.InterviewQuestion;
import com.vedant.jobtracker.entity.JobApplication;
import com.vedant.jobtracker.exception.ResourceNotFoundException;
import com.vedant.jobtracker.repository.InterviewQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewQuestionService {

    private final InterviewQuestionRepository interviewQuestionRepository;
    private final JobApplicationService jobApplicationService;
    private final AiAnalysisService aiAnalysisService;

    public InterviewQuestionService(InterviewQuestionRepository interviewQuestionRepository,
                                     JobApplicationService jobApplicationService,
                                     AiAnalysisService aiAnalysisService) {
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.jobApplicationService = jobApplicationService;
        this.aiAnalysisService = aiAnalysisService;
    }

    public List<InterviewQuestionResponse> generate(Long applicationId, String email) {
        // Reuses step 3's ownership check - no need to reimplement it here.
        JobApplication app = jobApplicationService.getOwnedApplication(applicationId, email);

        if (app.getJobDescription() == null || app.getMatchScore() == null) {
            throw new IllegalArgumentException("Run a match analysis on this application first.");
        }

        List<String> questions = aiAnalysisService.generateQuestions(
                app.getJobDescription(),
                app.getMissingSkills()
        );

        // Regenerating replaces the old set rather than piling up duplicates.
        interviewQuestionRepository.deleteAll(interviewQuestionRepository.findByJobApplicationId(applicationId));

        List<InterviewQuestion> saved = questions.stream()
                .map(text -> {
                    InterviewQuestion q = new InterviewQuestion();
                    q.setQuestion(text);
                    q.setJobApplication(app);
                    return interviewQuestionRepository.save(q);
                })
                .toList();

        return saved.stream().map(this::toResponse).toList();
    }

    public List<InterviewQuestionResponse> getAll(Long applicationId, String email) {
        jobApplicationService.getOwnedApplication(applicationId, email); // ownership check only
        return interviewQuestionRepository.findByJobApplicationId(applicationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InterviewQuestionResponse updatePracticed(Long applicationId, Long questionId, boolean practiced, String email) {
        jobApplicationService.getOwnedApplication(applicationId, email); // ownership check

        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Same "same 404 either way" pattern as job applications in step 3 -
        // a question that exists but belongs to a different application of
        // yours (or someone else's) is treated as not found, not forbidden.
        if (!question.getJobApplication().getId().equals(applicationId)) {
            throw new ResourceNotFoundException("Question not found");
        }

        question.setPracticed(practiced);
        return toResponse(interviewQuestionRepository.save(question));
    }

    private InterviewQuestionResponse toResponse(InterviewQuestion q) {
        return new InterviewQuestionResponse(q.getId(), q.getQuestion(), q.isPracticed());
    }
}

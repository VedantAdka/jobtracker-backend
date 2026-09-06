package com.vedant.jobtracker.dto;

import com.vedant.jobtracker.entity.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class JobApplicationResponse {

    private Long id;
    private String company;
    private String jobTitle;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String notes;
    private LocalDateTime createdAt;

    // Present only once "Analyze match" has been run for this application
    private String jobDescription;
    private Integer matchScore;
    private List<String> missingSkills;
    private String resumeSuggestions;
    private LocalDateTime analyzedAt;

    public JobApplicationResponse(Long id, String company, String jobTitle, ApplicationStatus status,
                                  LocalDate appliedDate, String notes, LocalDateTime createdAt,
                                  String jobDescription, Integer matchScore, List<String> missingSkills,
                                  String resumeSuggestions, LocalDateTime analyzedAt) {
        this.id = id;
        this.company = company;
        this.jobTitle = jobTitle;
        this.status = status;
        this.appliedDate = appliedDate;
        this.notes = notes;
        this.createdAt = createdAt;
        this.jobDescription = jobDescription;
        this.matchScore = matchScore;
        this.missingSkills = missingSkills;
        this.resumeSuggestions = resumeSuggestions;
        this.analyzedAt = analyzedAt;
    }

    public Long getId() { return id; }
    public String getCompany() { return company; }
    public String getJobTitle() { return jobTitle; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDate getAppliedDate() { return appliedDate; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getJobDescription() { return jobDescription; }
    public Integer getMatchScore() { return matchScore; }
    public List<String> getMissingSkills() { return missingSkills; }
    public String getResumeSuggestions() { return resumeSuggestions; }
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
}

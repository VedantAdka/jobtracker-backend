package com.vedant.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    private LocalDate appliedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Filled in only after "Analyze match" is run for this application.
    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    private Integer matchScore;

    @Column(columnDefinition = "TEXT")
    private String missingSkills; // stored comma-separated, exposed as a List in the response DTO

    @Column(columnDefinition = "TEXT")
    private String resumeSuggestions;

    private LocalDateTime analyzedAt;

    // Same cascade pattern as User -> JobApplication in step 1: delete the
    // application, its questions go with it.
    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewQuestion> interviewQuestions = new ArrayList<>();

    // Many applications belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public JobApplication() {}

    public JobApplication(String company, String jobTitle, LocalDate appliedDate, User user) {
        this.company = company;
        this.jobTitle = jobTitle;
        this.appliedDate = appliedDate;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

    public String getMissingSkills() { return missingSkills; }
    public void setMissingSkills(String missingSkills) { this.missingSkills = missingSkills; }

    public String getResumeSuggestions() { return resumeSuggestions; }
    public void setResumeSuggestions(String resumeSuggestions) { this.resumeSuggestions = resumeSuggestions; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }

    public List<InterviewQuestion> getInterviewQuestions() { return interviewQuestions; }
    public void setInterviewQuestions(List<InterviewQuestion> interviewQuestions) { this.interviewQuestions = interviewQuestions; }
}

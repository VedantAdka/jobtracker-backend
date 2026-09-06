package com.vedant.jobtracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    // Whether the user has checked this off as practiced - the whole
    // point of step 8's checklist.
    private boolean practiced = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    public InterviewQuestion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public boolean isPracticed() { return practiced; }
    public void setPracticed(boolean practiced) { this.practiced = practiced; }

    public JobApplication getJobApplication() { return jobApplication; }
    public void setJobApplication(JobApplication jobApplication) { this.jobApplication = jobApplication; }
}

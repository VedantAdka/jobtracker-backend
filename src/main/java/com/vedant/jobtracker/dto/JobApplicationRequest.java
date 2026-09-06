package com.vedant.jobtracker.dto;


import com.vedant.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class JobApplicationRequest {

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    // Optional - the service defaults this to APPLIED if left out
    private ApplicationStatus status;

    private LocalDate appliedDate;

    private String notes;

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
}

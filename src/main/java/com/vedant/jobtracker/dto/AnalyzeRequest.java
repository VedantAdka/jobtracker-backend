package com.vedant.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;

public class AnalyzeRequest {

    @NotBlank(message = "Job description is required")
    private String jobDescription;

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
}

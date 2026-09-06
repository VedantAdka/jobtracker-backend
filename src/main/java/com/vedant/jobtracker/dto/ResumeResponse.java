package com.vedant.jobtracker.dto;

import java.time.LocalDateTime;

public class ResumeResponse {

    private boolean hasResume;
    private String fileName;
    private LocalDateTime uploadedAt;
    private String preview;
    private Integer textLength;

    public ResumeResponse(boolean hasResume, String fileName, LocalDateTime uploadedAt,
                           String preview, Integer textLength) {
        this.hasResume = hasResume;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.preview = preview;
        this.textLength = textLength;
    }

    public boolean isHasResume() { return hasResume; }
    public String getFileName() { return fileName; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public String getPreview() { return preview; }
    public Integer getTextLength() { return textLength; }
}

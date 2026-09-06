package com.vedant.jobtracker.dto;

public class InterviewQuestionResponse {

    private Long id;
    private String question;
    private boolean practiced;

    public InterviewQuestionResponse(Long id, String question, boolean practiced) {
        this.id = id;
        this.question = question;
        this.practiced = practiced;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public boolean isPracticed() { return practiced; }
}

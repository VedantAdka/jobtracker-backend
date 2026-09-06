package com.vedant.jobtracker.exception;

// Covers every way the AI call can go wrong: not configured, network/auth
// failure calling GEMINI, or a response we couldn't parse.
public class AiServiceException extends RuntimeException {
    public AiServiceException(String message) {
        super(message);
    }
}

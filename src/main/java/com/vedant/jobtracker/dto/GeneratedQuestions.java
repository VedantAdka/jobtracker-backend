package com.vedant.jobtracker.dto;

import java.util.List;

// What we ask the AI to return when generating interview questions -
// same "record matches the JSON shape" trick as AnalysisResult.
public record GeneratedQuestions(List<String> questions) {}

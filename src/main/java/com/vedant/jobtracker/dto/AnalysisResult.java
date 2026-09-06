package com.vedant.jobtracker.dto;

import java.util.List;

// What we ask the AI to return, parsed straight out of its JSON response.
// A Java record works here because Jackson (bundled with Spring Boot 3.x)
// can deserialize JSON directly into records whose component names match
// the JSON keys - no extra annotations needed.
public record AnalysisResult(int matchScore, List<String> missingSkills, String suggestions) {}

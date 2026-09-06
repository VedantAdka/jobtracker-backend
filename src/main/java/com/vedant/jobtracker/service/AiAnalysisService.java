package com.vedant.jobtracker.service;


import com.vedant.jobtracker.dto.AnalysisResult;
import com.vedant.jobtracker.dto.GeneratedQuestions;
import com.vedant.jobtracker.exception.AiServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

// The ONLY class in the project that knows about the Gemini API. Two public
// methods now - analyze() from step 7, generateQuestions() new in step 8 -
// both built on the same shared private plumbing below, since asking Gemini
// for something and parsing its JSON reply is identical work either way.
@Service
public class AiAnalysisService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String model;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisResult analyze(String resumeText, String jobDescription) {
        String prompt = """
                You are analyzing how well a resume matches a job description.

                RESUME:
                %s

                JOB DESCRIPTION:
                %s

                Respond with ONLY a JSON object in exactly this shape:
                {"matchScore": <integer 0-100>, "missingSkills": ["skill1", "skill2"], "suggestions": "<2-3 sentences of specific advice>"}
                """.formatted(resumeText, jobDescription);

        try {
            return objectMapper.readValue(extractJson(callGemini(prompt)), AnalysisResult.class);
        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AiServiceException("Couldn't parse the AI's response. Try again.");
        }
    }

    public List<String> generateQuestions(String jobDescription, String missingSkills) {
        String skillsText = (missingSkills == null || missingSkills.isBlank())
                ? "(none identified)"
                : missingSkills;

        String prompt = """
                You are helping a candidate prepare for a job interview.

                JOB DESCRIPTION:
                %s

                SKILLS THE CANDIDATE IS WEAKEST ON:
                %s

                Generate exactly 6 realistic interview questions this candidate is likely
                to be asked for this role. Weight them toward the weak skills listed above
                where relevant, but also cover the role generally.

                Respond with ONLY a JSON object in exactly this shape:
                {"questions": ["question 1", "question 2", "question 3", "question 4", "question 5", "question 6"]}
                """.formatted(jobDescription, skillsText);

        try {
            return objectMapper.readValue(extractJson(callGemini(prompt)), GeneratedQuestions.class).questions();
        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AiServiceException("Couldn't parse the AI's response. Try again.");
        }
    }

    // Shared by both public methods - builds the request, calls Gemini,
    // and returns the raw generated text (before either method parses it
    // into its own specific shape).
    private String callGemini(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiServiceException(
                    "AI analysis isn't configured. Set the GEMINI_API_KEY environment variable and restart the app."
            );
        }

        String uri = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri(uri)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new AiServiceException("Couldn't reach the AI service: " + e.getMessage());
        }

        return extractText(response);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new AiServiceException("Unexpected response shape from the AI service.");
        }
    }

    // Defensive cleanup shared by both parse paths: strip stray markdown
    // fences, then grab just the {...} substring rather than trusting the
    // whole response is clean JSON.
    private String extractJson(String rawText) {
        String cleaned = rawText.replace("```json", "").replace("```", "").trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');

        if (start == -1 || end == -1 || end < start) {
            throw new AiServiceException("Couldn't parse the AI's response. Try again.");
        }
        return cleaned.substring(start, end + 1);
    }
}

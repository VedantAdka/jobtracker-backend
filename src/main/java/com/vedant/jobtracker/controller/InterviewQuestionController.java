package com.vedant.jobtracker.controller;

import com.vedant.jobtracker.dto.InterviewQuestionResponse;
import com.vedant.jobtracker.dto.UpdateQuestionRequest;
import com.vedant.jobtracker.service.InterviewQuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Nested under /applications/{applicationId}/questions - not in SecurityConfig's
// permitAll list, so protected automatically like everything else since step 3.
@RestController
@RequestMapping("/api/applications/{applicationId}/questions")
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;

    public InterviewQuestionController(InterviewQuestionService interviewQuestionService) {
        this.interviewQuestionService = interviewQuestionService;
    }

    @PostMapping("/generate")
    public List<InterviewQuestionResponse> generate(@PathVariable Long applicationId,
                                                      Authentication authentication) {
        return interviewQuestionService.generate(applicationId, authentication.getName());
    }

    @GetMapping
    public List<InterviewQuestionResponse> getAll(@PathVariable Long applicationId,
                                                   Authentication authentication) {
        return interviewQuestionService.getAll(applicationId, authentication.getName());
    }

    @PutMapping("/{questionId}")
    public InterviewQuestionResponse updatePracticed(@PathVariable Long applicationId,
                                                       @PathVariable Long questionId,
                                                       @RequestBody UpdateQuestionRequest request,
                                                       Authentication authentication) {
        return interviewQuestionService.updatePracticed(
                applicationId, questionId, request.isPracticed(), authentication.getName()
        );
    }
}

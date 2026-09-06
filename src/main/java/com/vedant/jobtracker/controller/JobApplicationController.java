package com.vedant.jobtracker.controller;

import com.vedant.jobtracker.dto.AnalyzeRequest;
import com.vedant.jobtracker.dto.JobApplicationRequest;
import com.vedant.jobtracker.dto.JobApplicationResponse;
import com.vedant.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Nothing here is in SecurityConfig's permitAll list, so every method
// below already requires a valid Bearer token - no extra config needed.
@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public List<JobApplicationResponse> getAll(Authentication authentication) {
        return jobApplicationService.getAll(authentication.getName());
    }

    @GetMapping("/{id}")
    public JobApplicationResponse getOne(@PathVariable Long id, Authentication authentication) {
        return jobApplicationService.getOne(id, authentication.getName());
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(@Valid @RequestBody JobApplicationRequest request,
                                                         Authentication authentication) {
        JobApplicationResponse created = jobApplicationService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public JobApplicationResponse update(@PathVariable Long id,
                                         @Valid @RequestBody JobApplicationRequest request,
                                         Authentication authentication) {
        return jobApplicationService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        jobApplicationService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/analyze")
    public JobApplicationResponse analyze(@PathVariable Long id,
                                          @Valid @RequestBody AnalyzeRequest request,
                                          Authentication authentication) {
        return jobApplicationService.analyze(id, request.getJobDescription(), authentication.getName());
    }
}

package com.vedant.jobtracker.controller;


import com.vedant.jobtracker.dto.ResumeResponse;
import com.vedant.jobtracker.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Not in SecurityConfig's permitAll list, so this requires a valid token
// automatically - same "deny by default" pattern as the applications endpoints.
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<ResumeResponse> upload(@RequestParam("file") MultipartFile file,
                                                 Authentication authentication) {
        return ResponseEntity.ok(resumeService.upload(file, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<ResumeResponse> get(Authentication authentication) {
        return ResponseEntity.ok(resumeService.getCurrent(authentication.getName()));
    }
}

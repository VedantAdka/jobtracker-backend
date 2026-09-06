package com.vedant.jobtracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class PingController {

    // Just to prove the app boots and is reachable before we add real endpoints.
    @GetMapping("/api/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok", "message", "Job tracker backend is running");
    }
}

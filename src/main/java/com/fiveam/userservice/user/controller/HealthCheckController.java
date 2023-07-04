package com.fiveam.userservice.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/health")
public class HealthCheckController {
    @GetMapping
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok("Health Check Success");
    }
}

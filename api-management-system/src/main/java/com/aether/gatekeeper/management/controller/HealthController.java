package com.aether.gatekeeper.management.controller;

import com.aether.gatekeeper.management.dto.response.HealthResponse;
import com.aether.gatekeeper.management.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {
    private final HealthService healthService;

    @Value("${info.service.name}")
    private String serviceName;



    @GetMapping
    public ResponseEntity<HealthResponse> healthCheck(){
        String dbStatus=healthService.getHealth();
        return ResponseEntity.ok(
                new HealthResponse(serviceName,"UP",dbStatus, LocalDateTime.now()));
    }

}

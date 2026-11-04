package com.aether.gatekeeper.smart_gateway_service.controller;

import com.aether.gatekeeper.smart_gateway_service.dto.response.HealthResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @Value("${info.service.name}")
    private String serviceName;

    @Value("${info.service.description}")
    private String serviceDesc;

    @GetMapping
    public ResponseEntity<HealthResponseDto> healthCheck(){
      return ResponseEntity.ok(
              new HealthResponseDto(serviceName,serviceDesc,"UP"));
    }
}

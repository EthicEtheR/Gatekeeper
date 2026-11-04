package com.aether.gatekeeper.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class HealthResponse {

    private String serviceName;
    private String serviceStatus;
    private String dbStatus;
    private LocalDateTime timeStamp;
}

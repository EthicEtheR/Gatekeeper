package com.aether.gatekeeper.smart_gateway_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HealthResponseDto {
    private String serviceName;
    private String serviceDescription;
    private String appStatus;
}

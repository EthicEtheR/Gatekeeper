package com.aether.gatekeeper.smart_gateway_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long expireInSecond;
    private String role;
    private Long organizationId;
}

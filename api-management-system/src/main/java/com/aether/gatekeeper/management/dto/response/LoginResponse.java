package com.aether.gatekeeper.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long expireInSecond;
    private String role;
    private Long organizationId;

}

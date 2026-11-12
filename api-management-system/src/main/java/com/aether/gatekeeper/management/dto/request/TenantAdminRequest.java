package com.aether.gatekeeper.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TenantAdminRequest {
    @NotBlank
    @Size(max=150)
    private String username;
    @NotBlank
    private String password;

    private Boolean  enabled=true;
}

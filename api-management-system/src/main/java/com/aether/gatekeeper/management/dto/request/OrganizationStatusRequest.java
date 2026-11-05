package com.aether.gatekeeper.management.dto.request;

import com.aether.gatekeeper.management.enums.OrganizationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationStatusRequest {

    @NotBlank
    private OrganizationStatus status;
}

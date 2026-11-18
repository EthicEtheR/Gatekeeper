package com.aether.gatekeeper.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RouteStatusRequest {

    @NotNull
    private Boolean status;
}

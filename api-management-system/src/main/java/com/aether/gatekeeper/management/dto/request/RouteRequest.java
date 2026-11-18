package com.aether.gatekeeper.management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RouteRequest {
    @NotBlank
    private String routeId;
    @NotBlank
    @Size(max=150)
    private String name;
    @NotBlank
    private String pathPattern;
    @NotBlank
    private String targetBaseUrl;
    @NotBlank
    private String targetPathPrefix;

    @NotNull
    @Min(value = 1, message = "Rate limit must be at least 1")
    @Max(value = 10000, message = "Rate limit cannot exceed 10000")
    private Integer requestsPerMinute;

    @Positive(message = "Timeout must be greater than 0")
    private Integer responseTimeoutMs;

    private boolean idempotencyEnabled;

    @NotEmpty
    private Set<@NotBlank String> allowedMethods
            = new HashSet<>();

    @NotNull
    private List<@Valid HeaderRuleRequest> headerRules
            = new ArrayList<>();
}

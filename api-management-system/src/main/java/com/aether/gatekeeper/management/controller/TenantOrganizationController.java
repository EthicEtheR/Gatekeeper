package com.aether.gatekeeper.management.controller;

import com.aether.gatekeeper.management.dto.request.TenantOrganizationRequest;
import com.aether.gatekeeper.management.dto.response.OrganizationResponse;
import com.aether.gatekeeper.management.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/org")
public class TenantOrganizationController {

    private final OrganizationService organizationService;

    public TenantOrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> updateOwnOrganization(
            @Valid
            @RequestBody TenantOrganizationRequest request){

        return ResponseEntity.ok(organizationService.updateOwnOrganization(request));

    }

    @GetMapping
    public ResponseEntity<OrganizationResponse> getOwnOrganization(){
        return ResponseEntity.ok(organizationService.getOwnOrganization());
    }



}

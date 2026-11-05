package com.aether.gatekeeper.management.controller;

import com.aether.gatekeeper.management.dto.request.OrganizationRequest;
import com.aether.gatekeeper.management.dto.request.OrganizationStatusRequest;
import com.aether.gatekeeper.management.dto.response.OrganizationResponse;
import com.aether.gatekeeper.management.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/org")
public class PlatformOrganizationController {
    private final OrganizationService organizationService;

    public PlatformOrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    //    here as platform-admin we will add CRUD methods endpoints
    @PostMapping
    public ResponseEntity<OrganizationResponse> addOrganization(@Valid @RequestBody OrganizationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.addOrganization(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id){
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }
    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getOrganizations(){
        return ResponseEntity.ok(organizationService.getOrganizations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> updateOrg(@Valid @RequestBody OrganizationRequest request,
                                                          @PathVariable Long id){
        return ResponseEntity.ok(organizationService.updatingOrganization(id,request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrganizationResponse> updatingStatus(@RequestBody
                                                               OrganizationStatusRequest statusRequest,
                                                               @PathVariable Long id){
            return ResponseEntity.ok(organizationService.changeStatus(id,statusRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrganization(@PathVariable Long id){
        organizationService.deleteOrganization(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Delete successfully");
    }


}

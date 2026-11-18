package com.aether.gatekeeper.management.util;

import com.aether.gatekeeper.management.entity.Organization;
import com.aether.gatekeeper.management.enums.OrganizationStatus;
import com.aether.gatekeeper.management.exception.ApiException;
import com.aether.gatekeeper.management.repository.OrganizationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {
    private final OrganizationRepository organizationRepository;

    public TenantContext(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization getOrganization(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof JwtAuthenticationToken))
            throw new ApiException("Tenant identity is missing ", HttpStatus.FORBIDDEN);

        Long organizationId=((JwtAuthenticationToken) authentication).getToken()
                .getClaim("organizationId");

        if(organizationId==null)
            throw new ApiException("Can not fetch organizationId from Tenant JwtAuthenticationToken ",
                    HttpStatus.FORBIDDEN);

        Organization org=organizationRepository.findById(organizationId)
                .orElseThrow(()->new ApiException("Organization is not exists ",HttpStatus.FORBIDDEN));

        if(! org.getStatus().equals(OrganizationStatus.ACTIVE))
            throw new ApiException("Organization is not ACTIVE ",HttpStatus.FORBIDDEN);

        return org;
    }
}

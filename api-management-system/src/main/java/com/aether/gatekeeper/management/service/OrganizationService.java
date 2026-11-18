package com.aether.gatekeeper.management.service;

import com.aether.gatekeeper.management.dto.request.TenantAdminRequest;
import com.aether.gatekeeper.management.dto.request.OrganizationRequest;
import com.aether.gatekeeper.management.dto.request.OrganizationStatusRequest;
import com.aether.gatekeeper.management.dto.request.TenantOrganizationRequest;
import com.aether.gatekeeper.management.dto.response.TenantAdminResponse;
import com.aether.gatekeeper.management.dto.response.OrganizationResponse;
import com.aether.gatekeeper.management.entity.ManagementUser;
import com.aether.gatekeeper.management.entity.Organization;
import com.aether.gatekeeper.management.entity.Role;
import com.aether.gatekeeper.management.enums.OrganizationStatus;
import com.aether.gatekeeper.management.exception.ApiException;
import com.aether.gatekeeper.management.repository.ManagementUserRepository;
import com.aether.gatekeeper.management.repository.OrganizationRepository;


import com.aether.gatekeeper.management.repository.RoleRepository;
import com.aether.gatekeeper.management.util.TenantContext;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService {
     private final OrganizationRepository organizationRepository;
     private final ManagementUserRepository managementUserRepository;
     private final RoleRepository roleRepository;
     private final PasswordEncoder passwordEncoder;
     private final TenantContext tenantContext;

    public OrganizationService(OrganizationRepository organizationRepository, ManagementUserRepository managementUserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, TenantContext tenantContext) {
        this.organizationRepository = organizationRepository;
        this.managementUserRepository = managementUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantContext = tenantContext;
    }

    @Transactional
    public OrganizationResponse addOrganization( OrganizationRequest request) {
        if(checkUnique(request))
            throw new ApiException("Organization name or slug already exists , Be Unique !! ",
                    HttpStatus.CONFLICT);

        Organization org=new Organization();
                  org.setSlug(request.getSlug());
                  org.setName(request.getName());
                  org.setStatus(OrganizationStatus.PENDING);
                  org.setCreatedAt(LocalDateTime.now());
                  org.setUpdatedAt(LocalDateTime.now());

               Organization saved=organizationRepository.save(org);

               return entityToOrgDto(saved);
    }
     @Transactional(readOnly = true)
    public  OrganizationResponse getOrganizationById(Long id) {
        Organization org=organizationRepository.findById(id)
                .orElseThrow(()->new ApiException("Organization is not found by this id : "+id,
                        HttpStatus.BAD_REQUEST));

        return entityToOrgDto(org);
    }

    public @Nullable List<OrganizationResponse> getOrganizations() {
        List<Organization> orgs=organizationRepository.findAll();
        List<OrganizationResponse> list=new ArrayList<>();

        for(Organization or: orgs){
            list.add(entityToOrgDto(or));
        }

        return list;
    }


    @Transactional
    public @Nullable OrganizationResponse updatingOrganization(Long id, OrganizationRequest request) {

        if(checkUnique(request))
            throw new ApiException("Organization name or slug already exists , Be Unique !! ",
                    HttpStatus.CONFLICT);


        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Organization is not found with given id: " + id,
                        HttpStatus.NOT_FOUND
                ));


        if(request.getName()!=null)
         org.setName(request.getName());

        if(request.getSlug()!=null)
          org.setSlug(request.getSlug());

        org.setUpdatedAt(LocalDateTime.now());

        return entityToOrgDto(org);

    }




    @Transactional
    public @Nullable OrganizationResponse changeStatus(Long id, OrganizationStatusRequest statusRequest) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Organization is not found with given id: " + id,
                        HttpStatus.NOT_FOUND
                ));
        if(statusRequest.getStatus().equals(OrganizationStatus.DELETED))
             throw new ApiException("We can change status to DELETED",HttpStatus.CONFLICT);

        org.setStatus(statusRequest.getStatus());
        org.setUpdatedAt(LocalDateTime.now());

        return entityToOrgDto(org);

    }

    @Transactional
    public void deleteOrganization(Long id) {

        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Organization is not found with given id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        org.setDeletedAt(LocalDateTime.now());
        org.setStatus(OrganizationStatus.DELETED);
        org.setUpdatedAt(LocalDateTime.now());
    }


//    ----------CRUD for TENET--------------------------->

    @Transactional
    public @Nullable TenantAdminResponse createTenant(Long orgId, TenantAdminRequest userRequest) {
        Long startedAt=System.nanoTime();

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiException(
                        "Organization is not found with given id: " + orgId,
                        HttpStatus.NOT_FOUND
                ));

        if(managementUserRepository.existsByOrganizationId(orgId))
            throw new ApiException("This Organization already have tenant admin : " + orgId,
                    HttpStatus.CONFLICT);

        if(managementUserRepository.existsByUsername(userRequest.getUsername()))
            throw new ApiException("User already exists with given username ",HttpStatus.CONFLICT);

        Role role=roleRepository.findByName("TENANT_ADMIN")
                .orElseThrow(()->new ApiException("Role is not exist ",HttpStatus.CONFLICT));

        ManagementUser user=new ManagementUser();
        user.setOrganization(org);
        user.setRole(role);
        user.setUsername(userRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        user.setEnabled(userRequest.getEnabled());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        managementUserRepository.save(user);


       return TenantAdminResponse.toDto(user);


    }

    public @Nullable TenantAdminResponse getTenant(Long orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiException(
                        "Organization is not found with given id: " + orgId,
                        HttpStatus.NOT_FOUND
                ));

        ManagementUser user= managementUserRepository.findByOrganizationId(orgId)
                .orElseThrow(()->new ApiException("Tenant admin not exists by given organization Id",
                        HttpStatus.CONFLICT));

        return TenantAdminResponse.toDto(user);
    }


    @Transactional
    public @Nullable TenantAdminResponse updateTenant(Long id, @Valid TenantAdminRequest request) {

        ManagementUser user=managementUserRepository.findByOrganizationId(id)
                .orElseThrow(()->new ApiException("Tenant admin is not exist ",HttpStatus.CONFLICT));

        if(!user.getUsername().equals(request.getUsername()) &&
                managementUserRepository.existsByUsername(request.getUsername()))
            throw new ApiException("User already exists ",HttpStatus.CONFLICT);

        user.setUsername(request.getUsername());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(request.getEnabled());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        return TenantAdminResponse.toDto(user);
    }

    @Transactional
    public void deleteTenant(Long id) {

        ManagementUser user=managementUserRepository.findByOrganizationId(id)
                .orElseThrow(()->new ApiException("Tenant admin is not exist ",HttpStatus.CONFLICT));

        managementUserRepository.delete(user);
    }

//    CRUD for Own Organization by tenant-admin-------------------------------

    @Transactional(readOnly = true)
    public @Nullable OrganizationResponse getOwnOrganization() {

        return entityToOrgDto(tenantContext.getOrganization());

    }


    @Transactional
    public @Nullable OrganizationResponse updateOwnOrganization(
            @Valid TenantOrganizationRequest request){

        Organization org=tenantContext.getOrganization();

        if(!org.getName().equalsIgnoreCase(request.getName()) &&
                organizationRepository.existsByNameIgnoreCase(request.getName()))
            throw new ApiException("Organization name already exists ",HttpStatus.CONFLICT);

        org.setName(request.getName());
        org.setUpdatedAt(LocalDateTime.now());

        return entityToOrgDto(org);

    }


//    helper methods------------------------------

    // TODO we have to improve checkUnique method logic ,can throw null pointer error

    private boolean checkUnique(OrganizationRequest request) {

        if(request.getName()!=null &&
                Boolean.TRUE.equals(organizationRepository.existsByNameIgnoreCase(request.getName())))
            return true;

        if(request.getSlug()!=null &&
                Boolean.TRUE.equals(organizationRepository.existsBySlugIgnoreCase(request.getSlug())))
            return true;

        return false;
    }

    private OrganizationResponse entityToOrgDto(Organization org){

        return  new OrganizationResponse(org.getId(),
                org.getName(),org.getSlug(),org.getStatus(),
                org.getCreatedAt(),org.getUpdatedAt());
    }
}

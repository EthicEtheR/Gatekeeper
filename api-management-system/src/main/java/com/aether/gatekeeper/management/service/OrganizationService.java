package com.aether.gatekeeper.management.service;

import com.aether.gatekeeper.management.dto.request.OrganizationRequest;
import com.aether.gatekeeper.management.dto.request.OrganizationStatusRequest;
import com.aether.gatekeeper.management.dto.response.OrganizationResponse;
import com.aether.gatekeeper.management.entity.Organization;
import com.aether.gatekeeper.management.enums.OrganizationStatus;
import com.aether.gatekeeper.management.exception.ApiException;
import com.aether.gatekeeper.management.repository.OrganizationRepository;


import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService {
     private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public OrganizationResponse addOrganization( OrganizationRequest request) {
        if(!isUnique(request))
            throw new ApiException("Organization name or slug already exists , Be Unique !! ",
                    HttpStatus.CONFLICT);

        Organization org=new Organization();
                  org.setSlug(request.getSlug());
                  org.setName(request.getName());
                  org.setStatus(OrganizationStatus.PENDING);
                  org.setCreatedAt(LocalDateTime.now());
                  org.setUpdatedAt(LocalDateTime.now());

               Organization saved=organizationRepository.save(org);

               return entityToDto(saved);
    }
     @Transactional(readOnly = true)
    public  OrganizationResponse getOrganizationById(Long id) {
        Organization org=organizationRepository.findById(id)
                .orElseThrow(()->new ApiException("Organization is not found by this id : "+id,
                        HttpStatus.BAD_REQUEST));

        return entityToDto(org);
    }

    public @Nullable List<OrganizationResponse> getOrganizations() {
        List<Organization> orgs=organizationRepository.findAll();
        List<OrganizationResponse> list=new ArrayList<>();

        for(Organization or: orgs){
            list.add(entityToDto(or));
        }

        return list;
    }


    @Transactional
    public @Nullable OrganizationResponse updatingOrganization(Long id, OrganizationRequest request) {

        if(!isUnique(request))
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

        return entityToDto(org);

    }

// TODO we have to improve checkUnique method logic ,can throw null pointer error

    private boolean isUnique(OrganizationRequest request) {

        if(request.getName()!=null)
             if(Boolean.TRUE.equals(organizationRepository.existsByNameIgnoreCase(request.getName())))
                 return false;

        if(request.getSlug()!=null)
            if( Boolean.TRUE.equals(organizationRepository.existsBySlugIgnoreCase(request.getSlug())))
                 return false;

        return true;
    }

    private OrganizationResponse entityToDto(Organization org){

        return  new OrganizationResponse(org.getId(),
                    org.getName(),org.getSlug(),org.getStatus(),
                    org.getCreatedAt(),org.getUpdatedAt());
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

        return entityToDto(org);

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
}

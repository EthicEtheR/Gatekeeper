package com.aether.gatekeeper.management.service;

import com.aether.gatekeeper.management.dto.request.HeaderRuleRequest;
import com.aether.gatekeeper.management.dto.request.RouteRequest;
import com.aether.gatekeeper.management.dto.request.RouteStatusRequest;
import com.aether.gatekeeper.management.dto.response.AuditRecordResponse;
import com.aether.gatekeeper.management.dto.response.RouteResponse;
import com.aether.gatekeeper.management.dto.response.RouteVersionResponse;
import com.aether.gatekeeper.management.entity.*;
import com.aether.gatekeeper.management.enums.OrganizationStatus;
import com.aether.gatekeeper.management.event.GatewayConfigurationEventService;
import com.aether.gatekeeper.management.exception.ApiException;
import com.aether.gatekeeper.management.repository.ApiRouteRepository;
import com.aether.gatekeeper.management.repository.ControlPlaneAuditRecordRepository;
import com.aether.gatekeeper.management.repository.RouteConfigurationVersionRepository;
import com.aether.gatekeeper.management.util.TenantContext;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RouteService {

     private final TenantContext tenantContext;
     private final ApiRouteRepository apiRouteRepository;
     private final ObjectMapper objectMapper;
     private  final RouteConfigurationVersionRepository versionRepository;
     private final ControlPlaneAuditRecordRepository auditRecordRepository;
     private final GatewayConfigurationEventService configurationChangeService;

    public RouteService(TenantContext tenantContext, ApiRouteRepository apiRouteRepository, ObjectMapper objectMapper, RouteConfigurationVersionRepository versionRepository, ControlPlaneAuditRecordRepository auditRecordRepository, GatewayConfigurationEventService configurationChangeService) {
        this.tenantContext = tenantContext;
        this.apiRouteRepository = apiRouteRepository;
        this.objectMapper = objectMapper;
        this.versionRepository = versionRepository;
        this.auditRecordRepository = auditRecordRepository;
        this.configurationChangeService = configurationChangeService;
    }

    @Transactional
    public @Nullable RouteResponse createTenantRoute(RouteRequest request) {
        long startedAt=System.nanoTime();
        Organization organization=tenantContext.getOrganization();

        validateRouteRequest(request,organization);

        if(apiRouteRepository.existsByOrganizationIdAndRouteId(organization.getId(),request.getRouteId()))
             throw new ApiException("Route already exists ", HttpStatus.CONFLICT);

        ApiRoute route=new ApiRoute();
        route.setOrganization(organization);
        route.setConfigurationVersion(1L);
        route.setCreatedAt(LocalDateTime.now());
        populateValues(request,route);

        apiRouteRepository.save(route);

        saveVersion(route,"CREATED");

        configurationChangeService.publish("CREATED",route.getOrganization().getId(),route.getRouteId(),
                route.getConfigurationVersion());

        return RouteResponse.entityToDto(route);
    }

    @Transactional(readOnly = true)
    public @Nullable RouteResponse getRouteById(Long id) {
        Organization organization=tenantContext.getOrganization();

        ApiRoute route=apiRouteRepository.findByIdAndOrganizationId(id,organization.getId())
                .orElseThrow(()->new ApiException("Route does not exists by given id "+id,
                        HttpStatus.NOT_FOUND));

        return RouteResponse.entityToDto(route);
    }


    public @Nullable List<RouteResponse> getAllRoutes() {

        Organization organization=tenantContext.getOrganization();

        List<ApiRoute> routes=apiRouteRepository.findAllByOrganizationId(organization.getId())
                .orElseThrow(()->new ApiException("Routes are not exists by given OrganizationId "+ organization.getId()
                        ,HttpStatus.NOT_FOUND));
        List<RouteResponse> responses=new ArrayList<>();

        for(ApiRoute route :routes){
            responses.add(RouteResponse.entityToDto(route));
        }

        return responses;
    }

    @Transactional
    public @Nullable RouteResponse updateRoute(Long id, @Valid RouteRequest request) {
        long startedAt=System.nanoTime();

        Organization organization=tenantContext.getOrganization();

        validateRouteRequest(request,organization);

        ApiRoute route=apiRouteRepository.findByIdAndOrganizationId(id, organization.getId())
                .orElseThrow(()->new ApiException("Route not found ",HttpStatus.NOT_FOUND));

        if(!route.getRouteId().equals(request.getRouteId())) {
            throw new ApiException( "routeId cannot be changed",HttpStatus.BAD_REQUEST);
        }

        route.setConfigurationVersion(route.getConfigurationVersion()+1L);
        populateValues(request,route);


//        apiRouteRepository.save(route);

        saveVersion(route,"UPDATED");

        //trigger event
        configurationChangeService.publish("UPDATED",route.getOrganization().getId(),route.getRouteId(),
                route.getConfigurationVersion());

        return RouteResponse.entityToDto(route);

    }

    @Transactional
    public @Nullable RouteResponse changeRouteStatus(Long id, @Valid RouteStatusRequest statusRequest) {
        long startedAt=System.nanoTime();

        Organization organization=tenantContext.getOrganization();

        ApiRoute route=apiRouteRepository.findByIdAndOrganizationId(id, organization.getId())
                .orElseThrow(()->new ApiException("Route not found ",HttpStatus.NOT_FOUND));

        route.setConfigurationVersion(route.getConfigurationVersion()+1L);
        route.setUpdatedAt(LocalDateTime.now());
        route.setActive(statusRequest.getStatus());

        String changeType=statusRequest.getStatus()? "ACTIVATED" :"DEACTIVATED";

        saveVersion(route,changeType);

        //trigger event
        configurationChangeService.publish(changeType,route.getOrganization().getId(),route.getRouteId(),
                route.getConfigurationVersion());

        return RouteResponse.entityToDto(route);

    }

    @Transactional
    public @Nullable String deleteRoute(Long id) {
        long startedAt=System.nanoTime();
        Organization organization=tenantContext.getOrganization();

        ApiRoute route=apiRouteRepository.findByIdAndOrganizationId(id, organization.getId())
                .orElseThrow(()->new ApiException("Route not found ",HttpStatus.NOT_FOUND));

        route.setConfigurationVersion(route.getConfigurationVersion()+1L);
        route.setUpdatedAt(LocalDateTime.now());

        apiRouteRepository.delete(route);

        saveVersion(route,"DELETED");

//        trigger event
        configurationChangeService.publish("DELETED",route.getOrganization().getId(),route.getRouteId(),
                route.getConfigurationVersion());

        return "Route removed successfully";

    }


//    method to get all routes and config for Gateway service-------

public  List<RouteResponse> getAllRoutesForGateway() {
       List<ApiRoute> routeList=apiRouteRepository
               .findAllByActiveTrueAndOrganizationStatus(OrganizationStatus.ACTIVE)
               .orElseThrow(()->new ApiException("Active route and Organization not exists ",HttpStatus.NOT_FOUND));

       List<RouteResponse> responses=new ArrayList<>();

       for(ApiRoute route:routeList){
          responses.add( RouteResponse.entityToDto(route));
       }

       return responses;
}

    @Transactional(readOnly = true)
    public List<RouteVersionResponse> getVersions(String routeId) {
        Organization organization = tenantContext.getOrganization();

        List<RouteVersionResponse> response = new ArrayList<RouteVersionResponse>();
        List<RouteConfigurationVersion> versions = versionRepository
                .findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(organization.getId(), routeId);

        for(RouteConfigurationVersion version : versions) {
            response.add(RouteVersionResponse.entityToDto(version));
        }
        return response;
    }


    @Transactional(readOnly = true)
    public List<AuditRecordResponse> getAuditRecords() {
        Organization organization = tenantContext.getOrganization();

        List<AuditRecordResponse> response = new ArrayList<AuditRecordResponse>();

        List<ControlPlaneAuditRecord> records = auditRecordRepository
                .findAllByOrganizationIdOrderByCreatedAtDesc(organization.getId());

        for(ControlPlaneAuditRecord record : records) {
            response.add(AuditRecordResponse.entityToDto(record));
        }
        return response;
    }






//helper methods----------------------------------
    private void populateValues(RouteRequest request,ApiRoute route) {

        route.setRouteId(request.getRouteId());
        route.setName(request.getName());
        route.setPathPattern(request.getPathPattern());
        route.setRequestsPerMinute(request.getRequestsPerMinute());
        route.setUpdatedAt(LocalDateTime.now());
        route.setResponseTimeoutMs(request.getResponseTimeoutMs());
        route.setTargetBaseUrl(request.getTargetBaseUrl());
        route.setTargetPathPrefix(request.getTargetPathPrefix());
        route.setIdempotencyEnabled(request.isIdempotencyEnabled());
        route.setRequestsPerMinute(request.getRequestsPerMinute());
        route.setActive(true);

        route.setAllowedMethods(normalizeMethods(request.getAllowedMethods()));

        route.getHeaderRules().clear();
        for (HeaderRuleRequest ruleRequest : request.getHeaderRules()) {
            RouteHeaderRule rule = new RouteHeaderRule();
            rule.setRoute(route);
            rule.setDirection(ruleRequest.getDirection().toUpperCase(Locale.ENGLISH));
            rule.setAction(ruleRequest.getAction().toUpperCase(Locale.ENGLISH));
            rule.setHeaderName(ruleRequest.getHeaderName());
            rule.setHeaderValue(ruleRequest.getHeaderValue());
            rule.setCreatedAt(LocalDateTime.now());
            rule.setUpdatedAt(LocalDateTime.now());
            route.getHeaderRules().add(rule);


        }

    }

    private Set<String> normalizeMethods(Set<String> methods) {
        Set<String> normalized = new HashSet<String>();
        for(String method : methods) {
            String value = method.toUpperCase(Locale.ENGLISH);
            try {
                HttpMethod.valueOf(value);
            }
            catch(IllegalArgumentException exception) {
                throw new ApiException("Unsupported HTTP method: " + method,HttpStatus.BAD_REQUEST);
            }
            normalized.add(value);
        }
        return normalized;
    }

    private void validateRouteRequest(RouteRequest request, Organization organization) {
        String tenantPrefix = "/" + organization.getSlug() + "/";

        if(!request.getPathPattern().startsWith(tenantPrefix)) {
            throw new ApiException(
                    "pathPattern must start with the tenant prefix " + tenantPrefix,HttpStatus.BAD_REQUEST);
        }

        if(!request.getTargetPathPrefix().startsWith("/")) {
            throw new ApiException( "targetPathPrefix must start with /",HttpStatus.BAD_REQUEST);
        }

        try {
            URI uri = URI.create(request.getTargetBaseUrl());
            if(!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException();
            }
        }
        catch(IllegalArgumentException exception) {
            throw new ApiException(
                    "targetBaseUrl must be a valid HTTP or HTTPS URL",HttpStatus.BAD_REQUEST);
        }

        normalizeMethods(request.getAllowedMethods());

        for(HeaderRuleRequest rule : request.getHeaderRules()) {
            String direction = rule.getDirection().toUpperCase(Locale.ENGLISH);
            String action = rule.getAction().toUpperCase(Locale.ENGLISH);
            if(!"REQUEST".equals(direction) && !"RESPONSE".equals(direction)) {
                throw new ApiException(
                        "Header direction must be REQUEST or RESPONSE",HttpStatus.BAD_REQUEST);
            }
            if(!"ADD".equals(action) && !"REMOVE".equals(action)) {
                throw new ApiException(
                        "Header action must be ADD or REMOVE",HttpStatus.BAD_REQUEST);
            }
            if("ADD".equals(action) &&
                    (rule.getHeaderValue() == null || rule.getHeaderValue().trim().isEmpty())) {
                throw new ApiException( "ADD header rules require headerValue",
                        HttpStatus.BAD_REQUEST);
            }
        }

    }

    @Transactional
    private void saveVersion(ApiRoute route,String changeType){
        RouteConfigurationVersion version=new RouteConfigurationVersion();

        version.setRouteId(route.getRouteId());
        version.setOrganization(route.getOrganization());
        version.setVersionNumber(route.getConfigurationVersion());
        version.setChangeType(changeType);
        version.setConfigurationSnapshot(objectMapper.writeValueAsString(RouteResponse.entityToDto(route)));
        version.setCreatedAt(LocalDateTime.now());

        versionRepository.save(version);
    }




}

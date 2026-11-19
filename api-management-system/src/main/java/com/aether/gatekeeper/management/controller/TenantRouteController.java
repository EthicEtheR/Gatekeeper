package com.aether.gatekeeper.management.controller;

import com.aether.gatekeeper.management.dto.request.RouteRequest;
import com.aether.gatekeeper.management.dto.request.RouteStatusRequest;
import com.aether.gatekeeper.management.dto.response.AuditRecordResponse;
import com.aether.gatekeeper.management.dto.response.RouteResponse;
import com.aether.gatekeeper.management.dto.response.RouteVersionResponse;
import com.aether.gatekeeper.management.service.RouteService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/route")
public class TenantRouteController {

    private final RouteService routeService;

    public TenantRouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest request){

        return ResponseEntity.status(HttpStatus.CREATED)
                 .body(routeService.createTenantRoute(request));

    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable Long id){

        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @GetMapping
    public ResponseEntity<List<RouteResponse>> getRoutes(){

        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponse> updateRoute(@PathVariable Long id,
                                                 @Valid @RequestBody RouteRequest request){
        return ResponseEntity.ok(routeService.updateRoute(id,request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RouteResponse> changeStatus(@PathVariable Long id,
                                                      @Valid @RequestBody RouteStatusRequest statusRequest){
        return ResponseEntity.ok(routeService.changeRouteStatus(id,statusRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(routeService.deleteRoute(id));
    }

    @GetMapping("/version/{routeId}")
    public ResponseEntity<List<RouteVersionResponse>> getVersions(@PathVariable String routeId){
        return ResponseEntity.ok(routeService.getVersions(routeId));
    }

    @GetMapping("/audit-records")
    public ResponseEntity<List<AuditRecordResponse>> getAuditRecords() {
        return ResponseEntity.ok(routeService.getAuditRecords());
    }


}

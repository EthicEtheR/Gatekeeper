package com.aether.gatekeeper.management.controller;

import com.aether.gatekeeper.management.dto.response.RouteResponse;
import com.aether.gatekeeper.management.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gateway/route")
public class GatewayConfigController {
    private final RouteService routeService;

    public GatewayConfigController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<RouteResponse>>  getAllRoutes(){

        return ResponseEntity.ok(routeService.getAllRoutesForGateway());
    }
}

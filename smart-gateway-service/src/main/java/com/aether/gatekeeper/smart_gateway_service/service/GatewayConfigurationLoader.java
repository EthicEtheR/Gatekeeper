package com.aether.gatekeeper.smart_gateway_service.service;

import com.aether.gatekeeper.smart_gateway_service.dto.response.GatewayRoute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GatewayConfigurationLoader implements ApplicationRunner {
    private final ApiManagementClient apiManagementClient;

    public GatewayConfigurationLoader(ApiManagementClient apiManagementClient) {
        this.apiManagementClient = apiManagementClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<GatewayRoute> routes=apiManagementClient.fetchActiveRoutes();

        for(GatewayRoute route: routes){
            log.info("Route : {}",route.getTargetPathPrefix());
        }
    }
}

package com.aether.gatekeeper.smart_gateway_service.event;

import com.aether.gatekeeper.smart_gateway_service.service.GatewayConfigurationLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConfigurationEventConsumer {
    private final GatewayConfigurationLoader configurationLoader;

    public ConfigurationEventConsumer(GatewayConfigurationLoader configurationLoader) {
        this.configurationLoader = configurationLoader;
    }

    @KafkaListener(topics = "${gateway.kafka.configuration-topic}",
            groupId = "${gateway.kafka.configuration-group}")
    void configurationChanged(String event){
         log.info("Gateway configuration changed event received {}",event);

         configurationLoader.refresh();

        log.info("Gateway configuration updated ");

    }
}

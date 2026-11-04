package com.aether.gatekeeper.analytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        return httpSecurity.authorizeHttpRequests(http->
                        http.requestMatchers("/health").permitAll()
                                .anyRequest().authenticated()
                )
                .build();
    }
}

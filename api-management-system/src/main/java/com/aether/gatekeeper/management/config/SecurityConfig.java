package com.aether.gatekeeper.management.config;

import ch.qos.logback.core.pattern.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http->
                http.requestMatchers("/health","/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o ->
                        o.jwt(j -> j.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



    @Bean
    JwtEncoder jwtEncoder(@Value("${jwt.secret.key}")String secret){
      SecretKey key=  new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");

      return NimbusJwtEncoder.withSecretKey(key).algorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secret.key}")String secret){
        SecretKey key=  new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter conv = new JwtGrantedAuthoritiesConverter();
        conv.setAuthoritiesClaimName("roles");
        conv.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConv = new JwtAuthenticationConverter();
        jwtConv.setJwtGrantedAuthoritiesConverter(conv);
        return jwtConv;
    }
}

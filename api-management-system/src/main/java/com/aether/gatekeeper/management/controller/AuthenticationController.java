package com.aether.gatekeeper.management.controller;

import com.aether.gatekeeper.management.dto.request.LoginRequest;
import com.aether.gatekeeper.management.dto.response.LoginResponse;
import com.aether.gatekeeper.management.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){

        return ResponseEntity.ok(authenticationService.login(request));

    }
}

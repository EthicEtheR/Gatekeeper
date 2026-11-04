package com.aether.gatekeeper.management.service;

import com.aether.gatekeeper.management.dto.request.LoginRequest;
import com.aether.gatekeeper.management.dto.response.LoginResponse;
import com.aether.gatekeeper.management.entity.ManagementUser;
import com.aether.gatekeeper.management.enums.OrganizationStatus;
import com.aether.gatekeeper.management.exception.ApiException;
import com.aether.gatekeeper.management.repository.ManagementUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Value("${jwt.expiry.in.second}")
    private Long expirationTime;
    private final ManagementUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;


    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        ManagementUser user=userRepository.findByUsername(request.getUsername())
                .orElseThrow(()->new ApiException("Please enter valid username or password",
                        HttpStatus.UNAUTHORIZED));

        if(!user.getEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
              throw new ApiException("Your credentials are not valid or your account is disabled ",
                      HttpStatus.UNAUTHORIZED);

        if(user.getOrganization()!=null && !user.getOrganization().getStatus().equals(OrganizationStatus.ACTIVE)){
            throw new ApiException("Your organization is not ACTIVE",
                    HttpStatus.FORBIDDEN);
        }

        Instant issuedAt=Instant.now();
        String role=user.getRole().getName();
        Long organizationId=(user.getOrganization()==null?null : user.getOrganization().getId());

       JwtClaimsSet.Builder claims=JwtClaimsSet.builder()
               .issuedAt(issuedAt)
               .issuer("gatekeeper-api-management")
               .expiresAt(issuedAt.plusSeconds(expirationTime))
               .subject(user.getUsername())
               .claim("role",role);

       if(organizationId!=null)
           claims.claim("organizationId",organizationId);

       JwsHeader header= JwsHeader.with(MacAlgorithm.HS256).build();
       String token=jwtEncoder.encode(JwtEncoderParameters.from(header,claims.build()))
               .getTokenValue();



        return new LoginResponse(token,"Bearer",expirationTime,role,organizationId);
    }
}

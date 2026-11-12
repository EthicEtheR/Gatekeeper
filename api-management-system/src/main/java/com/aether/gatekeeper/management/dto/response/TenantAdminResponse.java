package com.aether.gatekeeper.management.dto.response;

import com.aether.gatekeeper.management.entity.ManagementUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TenantAdminResponse {
    private Long id;
    private Long organizationId;
    private String username;
    private Boolean  enabled;


    public static TenantAdminResponse toDto(ManagementUser user){
        return new TenantAdminResponse(user.getId(), user.getOrganization().getId(),user.getUsername(),
                user.getEnabled());
    }

}


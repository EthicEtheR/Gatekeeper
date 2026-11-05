package com.aether.gatekeeper.management.dto.response;

import com.aether.gatekeeper.management.enums.OrganizationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrganizationResponse {

     private Long id;
     private String name;
     private String slug;
     private OrganizationStatus status;
     private LocalDateTime createdAt;
     private LocalDateTime updatedAt;

}

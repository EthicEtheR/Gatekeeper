package com.aether.gatekeeper.management.entity;

import com.aether.gatekeeper.management.enums.OrganizationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
@Data
public class Organization {
    @Id
    private Long id;
    private String name;
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}

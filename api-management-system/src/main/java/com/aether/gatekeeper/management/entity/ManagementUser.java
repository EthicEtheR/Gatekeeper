package com.aether.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "management_users")
@NoArgsConstructor
public class ManagementUser {
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="role_id")
    private Role role;

    private String username;
    private String passwordHash;
    private Boolean  enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

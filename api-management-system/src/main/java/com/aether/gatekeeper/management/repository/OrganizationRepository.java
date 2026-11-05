package com.aether.gatekeeper.management.repository;

import com.aether.gatekeeper.management.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    Boolean existsByNameIgnoreCase(String name);
    Boolean existsBySlugIgnoreCase(String slug);
}


package com.aether.gatekeeper.management.repository;

import com.aether.gatekeeper.management.entity.ManagementUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagementUserRepository extends JpaRepository<ManagementUser,Long> {

    Optional<ManagementUser> findByUsername(String username);
}

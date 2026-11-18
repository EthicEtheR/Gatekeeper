package com.aether.gatekeeper.management.repository;

import com.aether.gatekeeper.management.entity.ApiRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiRouteRepository extends JpaRepository<ApiRoute,Long> {

    boolean existsByOrganizationIdAndRouteId(Long organizationId, String routeId);

    Optional<ApiRoute> findByIdAndOrganizationId(Long id, Long orgId);
    Optional<List<ApiRoute>> findAllByOrganizationId(Long orgId);
}

package com.aether.gatekeeper.management.repository;

import com.aether.gatekeeper.management.entity.RouteConfigurationVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteConfigurationVersionRepository extends JpaRepository<RouteConfigurationVersion,Long> {

    List<RouteConfigurationVersion>
    findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(Long orgId,String routeId);
}

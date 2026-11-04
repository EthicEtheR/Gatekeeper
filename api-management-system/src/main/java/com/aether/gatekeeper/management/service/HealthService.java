package com.aether.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService {
    private final JdbcTemplate jdbcTemplate;


    public String getHealth() {
        String datasourceStatus="DOWN";

        try {
            Integer result=jdbcTemplate.queryForObject("SELECT 1",Integer.class);

            if(Integer.valueOf(1).equals(result))
                datasourceStatus="UP";
        } catch (DataAccessException e) {
            log.error("Mysql is fail to connect :{}",e.getMessage());
//            throw new RuntimeException(e);
        }

     return datasourceStatus;
    }
}

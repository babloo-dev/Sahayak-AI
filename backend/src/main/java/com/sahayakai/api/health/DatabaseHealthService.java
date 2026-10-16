package com.sahayakai.api.health;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseHealthService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DatabaseHealthResponse checkConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return DatabaseHealthResponse.up();
        } catch (DataAccessException exception) {
            return DatabaseHealthResponse.down();
        }
    }
}

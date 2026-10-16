package com.sahayakai.api.health;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseHealthServiceTest {

    @Test
    void reportsConnectedWhenDatabaseQuerySucceeds() {
        DatabaseHealthService service = new DatabaseHealthService(new StubJdbcTemplate(false));

        DatabaseHealthResponse response = service.checkConnection();

        assertEquals("UP", response.status());
        assertTrue(response.connected());
    }

    @Test
    void reportsUnavailableWithoutExposingDatabaseException() {
        DatabaseHealthService service = new DatabaseHealthService(new StubJdbcTemplate(true));

        DatabaseHealthResponse response = service.checkConnection();

        assertEquals("DOWN", response.status());
        assertFalse(response.connected());
        assertEquals("Sahayak AI database", response.service());
    }

    static final class StubJdbcTemplate extends JdbcTemplate {
        private final boolean unavailable;

        StubJdbcTemplate(boolean unavailable) {
            this.unavailable = unavailable;
        }

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType) {
            if (unavailable) {
                throw new DataAccessResourceFailureException("private connection detail");
            }
            return requiredType.cast(1);
        }
    }
}

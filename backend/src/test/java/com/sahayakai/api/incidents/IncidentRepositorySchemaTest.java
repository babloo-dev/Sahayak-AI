package com.sahayakai.api.incidents;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentRepositorySchemaTest {

    @Test
    void insertsIntoExistingSingularTableUsingExistingColumnNames() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        IncidentRepository repository = new IncidentRepository(jdbcTemplate);

        ControlCenterIncidentResponse response = repository.save(
                new IncidentCreateRequest("Flood", "Water entering homes", "Ward 4", "+910000000000"));

        assertTrue(jdbcTemplate.sql.startsWith("INSERT INTO incident (disaster_type, description, location, contact_number, status)"));
        assertEquals("Flood", jdbcTemplate.parameters.get(1));
        assertEquals("Water entering homes", jdbcTemplate.parameters.get(2));
        assertEquals("Ward 4", jdbcTemplate.parameters.get(3));
        assertEquals("+910000000000", jdbcTemplate.parameters.get(4));
        assertEquals("SUBMITTED", jdbcTemplate.parameters.get(5));
        assertEquals(73L, response.id());
        assertEquals("Flood", response.incidentType());
    }

    @Test
    void readsOnlyColumnsPresentInTheExistingIncidentSchema() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        IncidentRepository repository = new IncidentRepository(jdbcTemplate);

        repository.findAll("SUBMITTED");

        assertTrue(jdbcTemplate.sql.contains("FROM incident WHERE status = ?"));
        assertTrue(jdbcTemplate.sql.contains("disaster_type"));
        assertTrue(jdbcTemplate.sql.contains("contact_number"));
        assertTrue(jdbcTemplate.sql.contains("affected_people"));
        assertTrue(jdbcTemplate.sql.contains("assigned_team"));
        assertTrue(!jdbcTemplate.sql.contains("FROM incidents"));
        assertTrue(!jdbcTemplate.sql.contains("reported_at"));
        assertTrue(!jdbcTemplate.sql.contains("media_reference"));
        assertEquals("SUBMITTED", jdbcTemplate.queryParameters[0]);
    }

    @Test
    void updatesExistingStatusColumnOnSingularIncidentTableWithCurrentStatusGuard() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        IncidentRepository repository = new IncidentRepository(jdbcTemplate);

        boolean updated = repository.updateStatus(73L, "SUBMITTED", "TEAM_ASSIGNED");

        assertTrue(updated);
        assertEquals("UPDATE incident SET status = ? WHERE id = ? AND status = ?", jdbcTemplate.sql);
        assertEquals("TEAM_ASSIGNED", jdbcTemplate.updateParameters[0]);
        assertEquals(73L, jdbcTemplate.updateParameters[1]);
        assertEquals("SUBMITTED", jdbcTemplate.updateParameters[2]);
    }

    @Test
    void rescueTeamStatusUpdateIsConditionedOnCurrentAssignment() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        IncidentRepository repository = new IncidentRepository(jdbcTemplate);

        assertTrue(repository.updateStatusForTeam(73L, "TEAM_ASSIGNED", "TEAM_ON_THE_WAY", "North team"));
        assertEquals("UPDATE incident SET status = ? WHERE id = ? AND status = ? AND BINARY assigned_team = BINARY ?",
                jdbcTemplate.sql);
        assertEquals("North team", jdbcTemplate.updateParameters[3]);
    }

    @Test
    void assignsTeamAndAdvancesStatusUsingExistingIncidentColumns() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        IncidentRepository repository = new IncidentRepository(jdbcTemplate);

        boolean assigned = repository.assignTeam(73L, "SUBMITTED", "North response team", "TEAM_ASSIGNED");

        assertTrue(assigned);
        assertEquals("UPDATE incident SET assigned_team = ?, status = ? WHERE id = ? AND status = ?",
                jdbcTemplate.sql);
        assertEquals("North response team", jdbcTemplate.updateParameters[0]);
        assertEquals("TEAM_ASSIGNED", jdbcTemplate.updateParameters[1]);
        assertEquals(73L, jdbcTemplate.updateParameters[2]);
        assertEquals("SUBMITTED", jdbcTemplate.updateParameters[3]);
    }

    @Test
    void associatesNewIncidentWithCitizenInSeparateAdditiveTable() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        IncidentRepository repository = new IncidentRepository(jdbcTemplate);

        repository.associateWithCitizen(73L, "citizen-subject");

        assertEquals("INSERT INTO incident_owner (incident_id, citizen_subject) VALUES (?, ?)", jdbcTemplate.sql);
        assertEquals(73L, jdbcTemplate.updateParameters[0]);
        assertEquals("citizen-subject", jdbcTemplate.updateParameters[1]);
    }

    @Test
    void filtersCitizenAndRescueReadsByServerOwnedIdentity() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        IncidentRepository repository = new IncidentRepository(jdbcTemplate);

        repository.findAllForCitizen("citizen-subject", "SUBMITTED");
        assertTrue(jdbcTemplate.sql.contains("JOIN incident_owner o ON o.incident_id = i.id"));
        assertTrue(jdbcTemplate.sql.contains("BINARY o.citizen_subject = BINARY ?"));
        assertEquals("citizen-subject", jdbcTemplate.queryParameters[0]);
        assertEquals("SUBMITTED", jdbcTemplate.queryParameters[1]);

        repository.findAllForTeam("North response team", "TEAM_ASSIGNED");
        assertTrue(jdbcTemplate.sql.contains("WHERE BINARY i.assigned_team = BINARY ?"));
        assertEquals("North response team", jdbcTemplate.queryParameters[0]);
        assertEquals("TEAM_ASSIGNED", jdbcTemplate.queryParameters[1]);
    }

    private static final class RecordingJdbcTemplate extends JdbcTemplate {
        private String sql;
        private final Map<Integer, Object> parameters = new HashMap<>();
        private Object[] queryParameters = new Object[0];
        private Object[] updateParameters = new Object[0];

        @Override
        public int update(String query, Object... args) {
            sql = query;
            updateParameters = args;
            return 1;
        }

        @Override
        public int update(PreparedStatementCreator statementCreator, KeyHolder keyHolder) {
            try {
                statementCreator.createPreparedStatement(proxy(java.sql.Connection.class, (connectionProxy, method, args) -> {
                    if (method.getName().equals("prepareStatement")) {
                        sql = (String) args[0];
                        return proxy(java.sql.PreparedStatement.class, (statementProxy, setter, values) -> {
                            if (setter.getName().startsWith("set") && values != null && values.length > 1
                                    && values[0] instanceof Integer index) {
                                parameters.put(index, values[1]);
                            }
                            return null;
                        });
                    }
                    return null;
                }));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
            keyHolder.getKeyList().add(Map.of("GENERATED_KEY", 73L));
            return 1;
        }

        @Override
        public <T> List<T> query(String query, RowMapper<T> rowMapper, Object... args) {
            sql = query;
            queryParameters = args;
            return List.of();
        }

        @Override
        public <T> List<T> query(String query, RowMapper<T> rowMapper) {
            sql = query;
            queryParameters = new Object[0];
            return List.of();
        }

        @SuppressWarnings("unchecked")
        private <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
        }
    }
}

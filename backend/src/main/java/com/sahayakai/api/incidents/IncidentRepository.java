package com.sahayakai.api.incidents;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class IncidentRepository {

    private static final String SELECT_COLUMNS = "id, disaster_type, description, location, contact_number, status, "
            + "severity, affected_people, required_resources, priority, assigned_team";

    private final JdbcTemplate jdbcTemplate;

    public IncidentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ControlCenterIncidentResponse save(IncidentCreateRequest request) {
        String sql = "INSERT INTO incident (disaster_type, description, location, contact_number, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatementCreator statement = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, request.incidentType().trim());
            preparedStatement.setString(2, request.description().trim());
            preparedStatement.setString(3, request.location().trim());
            preparedStatement.setString(4, request.contactNumber().trim());
            preparedStatement.setString(5, "SUBMITTED");
            return preparedStatement;
        };
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(statement, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("The database did not return an incident ID");
        }
        return new ControlCenterIncidentResponse(key.longValue(), request.incidentType().trim(),
                request.description().trim(), request.location().trim(), request.contactNumber().trim(),
                "SUBMITTED", null, null, null, null, null);
    }

    public List<ControlCenterIncidentResponse> findAll(String status) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM incident"
                + (status == null ? " ORDER BY id DESC" : " WHERE status = ? ORDER BY id DESC");
        if (status == null) {
            return jdbcTemplate.query(sql, this::mapRow);
        }
        return jdbcTemplate.query(sql, this::mapRow, status);
    }

    public List<ControlCenterIncidentResponse> findAllForCitizen(String citizenSubject, String status) {
        String sql = "SELECT " + qualifyColumns("i")
                + " FROM incident i JOIN incident_owner o ON o.incident_id = i.id"
                + " WHERE BINARY o.citizen_subject = BINARY ?"
                + (status == null ? "" : " AND i.status = ?") + " ORDER BY i.id DESC";
        return status == null
                ? jdbcTemplate.query(sql, this::mapRow, citizenSubject)
                : jdbcTemplate.query(sql, this::mapRow, citizenSubject, status);
    }

    public List<ControlCenterIncidentResponse> findAllForTeam(String teamName, String status) {
        String sql = "SELECT " + qualifyColumns("i")
                + " FROM incident i WHERE BINARY i.assigned_team = BINARY ?"
                + (status == null ? "" : " AND i.status = ?") + " ORDER BY i.id DESC";
        return status == null
                ? jdbcTemplate.query(sql, this::mapRow, teamName)
                : jdbcTemplate.query(sql, this::mapRow, teamName, status);
    }

    public Optional<ControlCenterIncidentResponse> findById(long id) {
        List<ControlCenterIncidentResponse> results = jdbcTemplate.query(
                "SELECT " + SELECT_COLUMNS + " FROM incident WHERE id = ?", this::mapRow, id);
        return results.stream().findFirst();
    }

    public Optional<ControlCenterIncidentResponse> findByIdForCitizen(long id, String citizenSubject) {
        List<ControlCenterIncidentResponse> results = jdbcTemplate.query("SELECT " + qualifyColumns("i")
                + " FROM incident i JOIN incident_owner o ON o.incident_id = i.id"
                + " WHERE i.id = ? AND BINARY o.citizen_subject = BINARY ?", this::mapRow, id, citizenSubject);
        return results.stream().findFirst();
    }

    public Optional<ControlCenterIncidentResponse> findByIdForTeam(long id, String teamName) {
        List<ControlCenterIncidentResponse> results = jdbcTemplate.query("SELECT " + qualifyColumns("i")
                + " FROM incident i WHERE i.id = ? AND BINARY i.assigned_team = BINARY ?", this::mapRow, id, teamName);
        return results.stream().findFirst();
    }

    public void associateWithCitizen(long incidentId, String citizenSubject) {
        jdbcTemplate.update("INSERT INTO incident_owner (incident_id, citizen_subject) VALUES (?, ?)",
                incidentId, citizenSubject);
    }

    public boolean updateStatus(long id, String currentStatus, String newStatus) {
        int updated = jdbcTemplate.update(
                "UPDATE incident SET status = ? WHERE id = ? AND status = ?", newStatus, id, currentStatus);
        return updated == 1;
    }

    public boolean updateStatusForTeam(long id, String currentStatus, String newStatus, String teamName) {
        int updated = jdbcTemplate.update(
                "UPDATE incident SET status = ? WHERE id = ? AND status = ? AND BINARY assigned_team = BINARY ?",
                newStatus, id, currentStatus, teamName);
        return updated == 1;
    }

    public boolean assignTeam(long id, String currentStatus, String assignedTeam, String newStatus) {
        int updated = jdbcTemplate.update(
                "UPDATE incident SET assigned_team = ?, status = ? WHERE id = ? AND status = ?",
                assignedTeam, newStatus, id, currentStatus);
        return updated == 1;
    }

    private ControlCenterIncidentResponse mapRow(java.sql.ResultSet resultSet, int rowNumber)
            throws java.sql.SQLException {
        int affectedPeopleValue = resultSet.getInt("affected_people");
        Integer affectedPeople = resultSet.wasNull() ? null : affectedPeopleValue;
        return new ControlCenterIncidentResponse(
                resultSet.getLong("id"),
                resultSet.getString("disaster_type"),
                resultSet.getString("description"),
                resultSet.getString("location"),
                resultSet.getString("contact_number"),
                resultSet.getString("status"),
                resultSet.getString("severity"),
                affectedPeople,
                resultSet.getString("required_resources"),
                resultSet.getString("priority"),
                resultSet.getString("assigned_team"));
    }

    private String qualifyColumns(String alias) {
        return java.util.Arrays.stream(SELECT_COLUMNS.split(", "))
                .map(column -> alias + "." + column)
                .collect(java.util.stream.Collectors.joining(", "));
    }
}

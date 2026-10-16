package com.sahayakai.api.incidents;

public record CitizenIncidentResponse(
        long id,
        String incidentType,
        String description,
        String location,
        String status) {
}

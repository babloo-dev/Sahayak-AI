package com.sahayakai.api.incidents;

public record ControlCenterIncidentResponse(
        long id,
        String incidentType,
        String description,
        String location,
        String contactNumber,
        String status,
        String severity,
        Integer affectedPeople,
        String requiredResources,
        String priority,
        String assignedTeam) {
}

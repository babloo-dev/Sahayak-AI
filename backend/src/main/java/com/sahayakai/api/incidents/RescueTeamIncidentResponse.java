package com.sahayakai.api.incidents;

public record RescueTeamIncidentResponse(
        long id,
        String incidentType,
        String description,
        String location,
        String contactNumber,
        String status,
        String severity,
        Integer affectedPeople,
        String requiredResources,
        String assignedTeam) {
}

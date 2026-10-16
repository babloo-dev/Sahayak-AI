package com.sahayakai.api.incidents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IncidentAssignmentRequest(@NotBlank @Size(max = 255) String assignedTeam) {
}

package com.sahayakai.api.incidents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IncidentCreateRequest(
        @NotBlank @Size(max = 255) String incidentType,
        @NotBlank @Size(max = 255) String description,
        @NotBlank @Size(max = 255) String location,
        @NotBlank @Size(max = 255) String contactNumber) {
}

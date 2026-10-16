package com.sahayakai.api.incidents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IncidentStatusUpdateRequest(@NotBlank @Size(max = 64) String status) {
}

package com.sahayakai.api.incidents;

import java.util.Optional;

public enum IncidentStatus {
    SUBMITTED,
    TEAM_ASSIGNED,
    TEAM_ON_THE_WAY,
    RESCUE_OPERATION_STARTED,
    RESCUE_COMPLETED;

    public Optional<IncidentStatus> next() {
        int nextOrdinal = ordinal() + 1;
        return nextOrdinal < values().length ? Optional.of(values()[nextOrdinal]) : Optional.empty();
    }

    public static Optional<IncidentStatus> parse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(java.util.Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}

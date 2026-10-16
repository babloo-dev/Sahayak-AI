package com.sahayakai.api.incidents;

public class InvalidIncidentStatusException extends RuntimeException {
    public InvalidIncidentStatusException() {
        super("Unsupported incident status. Allowed values: SUBMITTED, TEAM_ASSIGNED, TEAM_ON_THE_WAY, "
                + "RESCUE_OPERATION_STARTED, RESCUE_COMPLETED");
    }
}

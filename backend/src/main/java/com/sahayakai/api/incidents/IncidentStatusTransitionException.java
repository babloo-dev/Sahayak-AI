package com.sahayakai.api.incidents;

public class IncidentStatusTransitionException extends RuntimeException {
    public IncidentStatusTransitionException(String message) {
        super(message);
    }
}

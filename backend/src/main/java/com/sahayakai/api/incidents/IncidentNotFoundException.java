package com.sahayakai.api.incidents;

public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(long id) {
        super("Incident " + id + " was not found");
    }
}

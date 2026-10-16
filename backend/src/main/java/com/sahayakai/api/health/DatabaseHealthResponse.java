package com.sahayakai.api.health;

public record DatabaseHealthResponse(String service, String status, boolean connected) {
    public static DatabaseHealthResponse up() {
        return new DatabaseHealthResponse("Sahayak AI database", "UP", true);
    }

    public static DatabaseHealthResponse down() {
        return new DatabaseHealthResponse("Sahayak AI database", "DOWN", false);
    }
}

package com.sahayakai.api.auth;

public record AuthenticatedUser(String subject, ApiRole role) {
}

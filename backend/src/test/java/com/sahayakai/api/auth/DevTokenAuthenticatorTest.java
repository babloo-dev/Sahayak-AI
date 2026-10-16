package com.sahayakai.api.auth;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DevTokenAuthenticatorTest {

    @Test
    void mapsConfiguredTokensToServerOwnedRolesOnlyInLocalProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("local");
        DevTokenAuthenticator authenticator = authenticator(environment, true);

        assertEquals(new AuthenticatedUser("citizen-subject", ApiRole.CITIZEN),
                authenticator.authenticate(citizenToken()).orElseThrow());
        assertEquals(new AuthenticatedUser("North response team", ApiRole.RESCUE_TEAM),
                authenticator.authenticate(teamToken()).orElseThrow());
        assertEquals(new AuthenticatedUser("admin-subject", ApiRole.ADMIN),
                authenticator.authenticate(adminToken()).orElseThrow());
        assertTrue(authenticator.authenticate(adminToken() + " ").isEmpty());
    }

    @Test
    void refusesTokensWhenDisabledOrOutsideLocalProfile() {
        MockEnvironment production = new MockEnvironment();
        production.setActiveProfiles("production");
        assertTrue(authenticator(production, true).authenticate(citizenToken()).isEmpty());

        MockEnvironment local = new MockEnvironment();
        local.setActiveProfiles("local");
        assertTrue(authenticator(local, false).authenticate(citizenToken()).isEmpty());
    }

    private DevTokenAuthenticator authenticator(MockEnvironment environment, boolean enabled) {
        return new DevTokenAuthenticator(environment, enabled,
                citizenToken(), "citizen-subject",
                teamToken(), "North response team",
                adminToken(), "admin-subject");
    }

    private String citizenToken() { return "citizen-test-token-value-".repeat(2); }
    private String teamToken() { return "rescue-test-token-value-".repeat(2); }
    private String adminToken() { return "admin-test-token-value-".repeat(2); }
}

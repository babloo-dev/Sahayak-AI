package com.sahayakai.api.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiAuthenticationFilterTest {

    @Test
    void rejectsIncidentRequestWithoutBearerToken() throws ServletException, IOException {
        ApiAuthenticationFilter filter = new ApiAuthenticationFilter(authenticator());
        MockHttpServletRequest request = request("GET", "/api/incidents");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> { });

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Authentication is required"));
    }

    @Test
    void authenticatesBearerTokenAndAddsServerOwnedPrincipal() throws ServletException, IOException {
        ApiAuthenticationFilter filter = new ApiAuthenticationFilter(authenticator());
        MockHttpServletRequest request = request("GET", "/api/incidents");
        request.addHeader("Authorization", "Bearer " + citizenToken());
        request.addHeader("X-Role", "ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> { });

        assertEquals(200, response.getStatus());
        assertEquals(new AuthenticatedUser("citizen-subject", ApiRole.CITIZEN),
                request.getAttribute(ApiAuthenticationFilter.USER_ATTRIBUTE));
    }

    @Test
    void leavesHealthAndCorsPreflightUnauthenticated() throws ServletException, IOException {
        ApiAuthenticationFilter filter = new ApiAuthenticationFilter(authenticator());
        MockHttpServletResponse healthResponse = new MockHttpServletResponse();
        filter.doFilter(request("GET", "/api/health"), healthResponse, (req, res) -> { });
        assertEquals(200, healthResponse.getStatus());

        MockHttpServletResponse optionsResponse = new MockHttpServletResponse();
        filter.doFilter(request("OPTIONS", "/api/incidents"), optionsResponse, (req, res) -> { });
        assertEquals(200, optionsResponse.getStatus());
    }

    private MockHttpServletRequest request(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setRequestURI(path);
        return request;
    }

    private DevTokenAuthenticator authenticator() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("local");
        return new DevTokenAuthenticator(environment, true,
                citizenToken(), "citizen-subject",
                "rescue-test-token-value-".repeat(2), "North response team",
                "admin-test-token-value-".repeat(2), "admin-subject");
    }

    private String citizenToken() { return "citizen-test-token-value-".repeat(2); }
}

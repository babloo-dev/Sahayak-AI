package com.sahayakai.api.auth;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest {

    @Test
    void returnsRoleDerivedByBackendAuthentication() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController()).build();

        mockMvc.perform(get("/api/auth/me").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE,
                        new AuthenticatedUser("local-admin", ApiRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.subject").doesNotExist());
    }
}

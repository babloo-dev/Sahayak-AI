package com.sahayakai.api.health;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DatabaseHealthControllerTest {

    @Test
    void returnsConnectedStatusWhenDatabaseIsAvailable() throws Exception {
        MockMvc mockMvc = controllerWithDatabaseState(false);

        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("Sahayak AI database"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.connected").value(true));
    }

    @Test
    void returnsSafeUnavailableStatusWhenDatabaseIsDown() throws Exception {
        MockMvc mockMvc = controllerWithDatabaseState(true);

        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.service").value("Sahayak AI database"))
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.connected").value(false))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    private MockMvc controllerWithDatabaseState(boolean unavailable) {
        DatabaseHealthService service = new DatabaseHealthService(
                new DatabaseHealthServiceTest.StubJdbcTemplate(unavailable));
        return MockMvcBuilders.standaloneSetup(new DatabaseHealthController(service)).build();
    }
}

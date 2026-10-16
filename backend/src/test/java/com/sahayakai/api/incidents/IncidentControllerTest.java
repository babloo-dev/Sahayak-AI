package com.sahayakai.api.incidents;

import java.util.List;
import java.util.Optional;

import com.sahayakai.api.errors.ApiExceptionHandler;
import com.sahayakai.api.auth.ApiAuthenticationFilter;
import com.sahayakai.api.auth.ApiRole;
import com.sahayakai.api.auth.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncidentControllerTest {

    private final StubIncidentRepository repository = new StubIncidentRepository();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new IncidentController(new IncidentService(repository)))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .defaultRequest(get("/").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE,
                        new AuthenticatedUser("local-admin", ApiRole.ADMIN)))
                .build();
    }

    @Test
    void createsIncidentAndReturnsCitizenReceiptWithoutContactDetails() throws Exception {
        repository.created = sampleIncident(42);

        mockMvc.perform(post("/api/incidents").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE,
                                new AuthenticatedUser("citizen-1", ApiRole.CITIZEN))
                        .contentType("application/json")
                        .content("""
                                {"incidentType":"Flood","description":"Water entering homes",
                                "location":"Ward 4, Riverside","contactNumber":"+910000000000"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/incidents/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.contactNumber").doesNotExist())
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.reportedAt").doesNotExist())
                .andExpect(jsonPath("$.mediaReference").doesNotExist());
        assertEquals("citizen-1", repository.associatedCitizen);
    }

    @Test
    void rejectsMissingRequiredFieldsWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/incidents")
                        .contentType("application/json")
                        .content("{\"incidentType\":\"Flood\",\"description\":\" \",\"location\":\"\",\"contactNumber\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.description").exists())
                .andExpect(jsonPath("$.fields.location").exists())
                .andExpect(jsonPath("$.fields.contactNumber").exists());
    }

    @Test
    void rejectsValuesLongerThanTheExistingVarcharColumns() throws Exception {
        String longDescription = "x".repeat(256);
        String body = "{\"incidentType\":\"Flood\",\"description\":\"%s\",\"location\":\"Ward 4\",\"contactNumber\":\"0000000\"}"
                .formatted(longDescription);

        mockMvc.perform(post("/api/incidents").contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.description").exists());
    }

    @Test
    void listsIncidentsAndSupportsStatusFilter() throws Exception {
        repository.created = sampleIncident(7);

        mockMvc.perform(get("/api/incidents").param("status", "submitted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].contactNumber").value("+910000000000"))
                .andExpect(jsonPath("$[0].severity").value("HIGH"))
                .andExpect(jsonPath("$[0].affectedPeople").value(3))
                .andExpect(jsonPath("$[0].assignedTeam").value("North team"));

        assertEquals("SUBMITTED", repository.receivedStatus);
    }

    @Test
    void returnsIncidentByIdAndReportsMissingIncident() throws Exception {
        repository.created = sampleIncident(11);
        mockMvc.perform(get("/api/incidents/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.incidentType").value("Flood"));

        mockMvc.perform(get("/api/incidents/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Incident 99 was not found"));
    }

    @Test
    void advancesIncidentStatusAndReturnsUpdatedControlCenterRecord() throws Exception {
        repository.created = sampleIncident(15);

        mockMvc.perform(patch("/api/incidents/15/status")
                        .contentType("application/json")
                        .content("{\"status\":\"TEAM_ASSIGNED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.status").value("TEAM_ASSIGNED"));
        assertEquals("TEAM_ASSIGNED", repository.created.status());
    }

    @Test
    void rejectsUnsupportedIncidentStatus() throws Exception {
        repository.created = sampleIncident(16);

        mockMvc.perform(patch("/api/incidents/16/status")
                        .contentType("application/json")
                        .content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Unsupported incident status")));
        assertEquals("SUBMITTED", repository.created.status());
    }

    @Test
    void rejectsOutOfOrderStatusTransition() throws Exception {
        repository.created = sampleIncident(17);

        mockMvc.perform(patch("/api/incidents/17/status")
                        .contentType("application/json")
                        .content("{\"status\":\"RESCUE_COMPLETED\"}"))
                .andExpect(status().isConflict());
        assertEquals("SUBMITTED", repository.created.status());
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingIncident() throws Exception {
        mockMvc.perform(patch("/api/incidents/999/status")
                        .contentType("application/json")
                        .content("{\"status\":\"TEAM_ASSIGNED\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Incident 999 was not found"));
    }

    @Test
    void assignsTeamAndMovesIncidentToTeamAssigned() throws Exception {
        repository.created = sampleIncident(18);

        mockMvc.perform(patch("/api/incidents/18/assignment")
                        .contentType("application/json")
                        .content("{\"assignedTeam\":\"North response team\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(18))
                .andExpect(jsonPath("$.assignedTeam").value("North response team"))
                .andExpect(jsonPath("$.status").value("TEAM_ASSIGNED"));
        assertEquals("North response team", repository.created.assignedTeam());
        assertEquals("TEAM_ASSIGNED", repository.created.status());
    }

    @Test
    void rejectsBlankTeamAssignment() throws Exception {
        repository.created = sampleIncident(19);

        mockMvc.perform(patch("/api/incidents/19/assignment")
                        .contentType("application/json")
                        .content("{\"assignedTeam\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.assignedTeam").exists());
        assertEquals("SUBMITTED", repository.created.status());
    }

    @Test
    void rejectsTeamNameLongerThanExistingColumn() throws Exception {
        repository.created = sampleIncident(20);

        mockMvc.perform(patch("/api/incidents/20/assignment")
                        .contentType("application/json")
                        .content("{\"assignedTeam\":\"%s\"}".formatted("x".repeat(256))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.assignedTeam").exists());
    }

    @Test
    void rejectsAssignmentWhenIncidentHasAdvancedPastSubmitted() throws Exception {
        repository.created = new ControlCenterIncidentResponse(21, "Flood", "Water", "Ward 4",
                "0000000000", "TEAM_ON_THE_WAY", "HIGH", 3, "Pumps", "URGENT", null);

        mockMvc.perform(patch("/api/incidents/21/assignment")
                        .contentType("application/json")
                        .content("{\"assignedTeam\":\"North response team\"}"))
                .andExpect(status().isConflict());
        assertEquals(null, repository.created.assignedTeam());
        assertEquals("TEAM_ON_THE_WAY", repository.created.status());
    }

    @Test
    void returnsNotFoundWhenAssigningMissingIncident() throws Exception {
        mockMvc.perform(patch("/api/incidents/999/assignment")
                        .contentType("application/json")
                        .content("{\"assignedTeam\":\"North response team\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Incident 999 was not found"));
    }

    @Test
    void citizenCanOnlyReadOwnReportsAndGetsNoContactOrAdminFields() throws Exception {
        repository.created = sampleIncident(31);
        repository.associatedCitizen = "citizen-1";
        AuthenticatedUser citizen = new AuthenticatedUser("citizen-1", ApiRole.CITIZEN);

        mockMvc.perform(get("/api/incidents").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE, citizen))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(31))
                .andExpect(jsonPath("$[0].contactNumber").doesNotExist())
                .andExpect(jsonPath("$[0].severity").doesNotExist())
                .andExpect(jsonPath("$[0].assignedTeam").doesNotExist());

        mockMvc.perform(get("/api/incidents/31").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE, citizen))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactNumber").doesNotExist())
                .andExpect(jsonPath("$.requiredResources").doesNotExist());

        mockMvc.perform(get("/api/incidents/31").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE,
                        new AuthenticatedUser("another-citizen", ApiRole.CITIZEN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void citizenCannotChangeStatusOrAssignTeams() throws Exception {
        repository.created = sampleIncident(32);
        AuthenticatedUser citizen = new AuthenticatedUser("citizen-1", ApiRole.CITIZEN);

        mockMvc.perform(patch("/api/incidents/32/status")
                        .requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE, citizen)
                        .contentType("application/json").content("{\"status\":\"TEAM_ASSIGNED\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/incidents/32/assignment")
                        .requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE, citizen)
                        .contentType("application/json").content("{\"assignedTeam\":\"North team\"}"))
                .andExpect(status().isForbidden());
        assertEquals("SUBMITTED", repository.created.status());
    }

    @Test
    void rescueTeamCanReadAndAdvanceOnlyItsAssignedIncident() throws Exception {
        repository.created = new ControlCenterIncidentResponse(33, "Flood", "Water", "Ward 4",
                "0000000000", "TEAM_ASSIGNED", "HIGH", 3, "Pumps", "URGENT", "North team");
        AuthenticatedUser team = new AuthenticatedUser("North team", ApiRole.RESCUE_TEAM);

        mockMvc.perform(get("/api/incidents/33").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE, team))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactNumber").value("0000000000"));
        mockMvc.perform(get("/api/incidents/33").requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE,
                        new AuthenticatedUser("South team", ApiRole.RESCUE_TEAM)))
                .andExpect(status().isNotFound());
        mockMvc.perform(patch("/api/incidents/33/status")
                        .requestAttr(ApiAuthenticationFilter.USER_ATTRIBUTE, team)
                        .contentType("application/json").content("{\"status\":\"TEAM_ON_THE_WAY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TEAM_ON_THE_WAY"));
    }

    private ControlCenterIncidentResponse sampleIncident(long id) {
        return new ControlCenterIncidentResponse(id, "Flood", "Water entering homes", "Ward 4, Riverside",
                "+910000000000", "SUBMITTED", "HIGH", 3, "Water pumps", "URGENT", "North team");
    }

    private static final class StubIncidentRepository extends IncidentRepository {
        private ControlCenterIncidentResponse created;
        private String receivedStatus;
        private String associatedCitizen;

        private StubIncidentRepository() {
            super(new JdbcTemplate());
        }

        @Override
        public ControlCenterIncidentResponse save(IncidentCreateRequest request) {
            return created;
        }

        @Override
        public void associateWithCitizen(long incidentId, String citizenSubject) {
            associatedCitizen = citizenSubject;
        }

        @Override
        public List<ControlCenterIncidentResponse> findAllForCitizen(String citizenSubject, String status) {
            return created != null && citizenSubject.equals(associatedCitizen) ? List.of(created) : List.of();
        }

        @Override
        public List<ControlCenterIncidentResponse> findAllForTeam(String teamName, String status) {
            return created != null && teamName.equals(created.assignedTeam()) ? List.of(created) : List.of();
        }

        @Override
        public List<ControlCenterIncidentResponse> findAll(String status) {
            receivedStatus = status;
            return created == null ? List.of() : List.of(created);
        }

        @Override
        public Optional<ControlCenterIncidentResponse> findById(long id) {
            return created != null && created.id() == id ? Optional.of(created) : Optional.empty();
        }

        @Override
        public Optional<ControlCenterIncidentResponse> findByIdForCitizen(long id, String citizenSubject) {
            return created != null && created.id() == id && citizenSubject.equals(associatedCitizen)
                    ? Optional.of(created) : Optional.empty();
        }

        @Override
        public Optional<ControlCenterIncidentResponse> findByIdForTeam(long id, String teamName) {
            return created != null && created.id() == id && teamName.equals(created.assignedTeam())
                    ? Optional.of(created) : Optional.empty();
        }

        @Override
        public boolean updateStatus(long id, String currentStatus, String newStatus) {
            if (created == null || created.id() != id || !created.status().equals(currentStatus)) {
                return false;
            }
            created = new ControlCenterIncidentResponse(created.id(), created.incidentType(), created.description(),
                    created.location(), created.contactNumber(), newStatus, created.severity(),
                    created.affectedPeople(), created.requiredResources(), created.priority(), created.assignedTeam());
            return true;
        }

        @Override
        public boolean updateStatusForTeam(long id, String currentStatus, String newStatus, String teamName) {
            if (created == null || created.id() != id || !created.status().equals(currentStatus)
                    || !teamName.equals(created.assignedTeam())) {
                return false;
            }
            return updateStatus(id, currentStatus, newStatus);
        }

        @Override
        public boolean assignTeam(long id, String currentStatus, String assignedTeam, String newStatus) {
            if (created == null || created.id() != id || !created.status().equals(currentStatus)) {
                return false;
            }
            created = new ControlCenterIncidentResponse(created.id(), created.incidentType(), created.description(),
                    created.location(), created.contactNumber(), newStatus, created.severity(),
                    created.affectedPeople(), created.requiredResources(), created.priority(), assignedTeam);
            return true;
        }
    }
}

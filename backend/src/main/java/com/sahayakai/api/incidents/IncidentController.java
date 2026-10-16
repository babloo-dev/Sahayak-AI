package com.sahayakai.api.incidents;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.sahayakai.api.auth.ApiAuthenticationFilter;
import com.sahayakai.api.auth.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<IncidentCreateReceipt> create(
            @Valid @RequestBody IncidentCreateRequest request,
            @org.springframework.web.bind.annotation.RequestAttribute(ApiAuthenticationFilter.USER_ATTRIBUTE) AuthenticatedUser user) {
        IncidentCreateReceipt receipt = incidentService.create(request, user);
        return ResponseEntity.created(URI.create("/api/incidents/" + receipt.id())).body(receipt);
    }

    @GetMapping
    public List<?> findAll(
            @RequestParam(required = false) @Size(max = 32) String status,
            @org.springframework.web.bind.annotation.RequestAttribute(ApiAuthenticationFilter.USER_ATTRIBUTE) AuthenticatedUser user) {
        return incidentService.findAll(status == null || status.isBlank() ? null : status.trim().toUpperCase(), user);
    }

    @GetMapping("/{id}")
    public Object findById(@PathVariable @Positive long id,
            @org.springframework.web.bind.annotation.RequestAttribute(ApiAuthenticationFilter.USER_ATTRIBUTE) AuthenticatedUser user) {
        return incidentService.findById(id, user);
    }

    @PatchMapping("/{id}/status")
    public Object updateStatus(
            @PathVariable @Positive long id,
            @Valid @RequestBody IncidentStatusUpdateRequest request,
            @org.springframework.web.bind.annotation.RequestAttribute(ApiAuthenticationFilter.USER_ATTRIBUTE) AuthenticatedUser user) {
        return incidentService.updateStatus(id, request, user);
    }

    @PatchMapping("/{id}/assignment")
    public ControlCenterIncidentResponse assignTeam(
            @PathVariable @Positive long id,
            @Valid @RequestBody IncidentAssignmentRequest request,
            @org.springframework.web.bind.annotation.RequestAttribute(ApiAuthenticationFilter.USER_ATTRIBUTE) AuthenticatedUser user) {
        return incidentService.assignTeam(id, request, user);
    }
}

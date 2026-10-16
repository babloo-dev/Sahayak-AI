package com.sahayakai.api.incidents;

import java.util.List;

import com.sahayakai.api.auth.ApiRole;
import com.sahayakai.api.auth.AuthenticatedUser;
import com.sahayakai.api.auth.ForbiddenOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public IncidentCreateReceipt create(IncidentCreateRequest request, AuthenticatedUser user) {
        requireRole(user, ApiRole.CITIZEN);
        ControlCenterIncidentResponse saved = incidentRepository.save(request);
        incidentRepository.associateWithCitizen(saved.id(), user.subject());
        return new IncidentCreateReceipt(saved.id(), saved.status());
    }

    public List<?> findAll(String status, AuthenticatedUser user) {
        return switch (user.role()) {
            case ADMIN -> incidentRepository.findAll(status);
            case CITIZEN -> incidentRepository.findAllForCitizen(user.subject(), status).stream()
                    .map(this::toCitizenResponse).toList();
            case RESCUE_TEAM -> incidentRepository.findAllForTeam(user.subject(), status).stream()
                    .map(this::toRescueResponse).toList();
        };
    }

    public Object findById(long id, AuthenticatedUser user) {
        return switch (user.role()) {
            case ADMIN -> incidentRepository.findById(id).orElseThrow(() -> new IncidentNotFoundException(id));
            case CITIZEN -> incidentRepository.findByIdForCitizen(id, user.subject())
                    .map(this::toCitizenResponse).orElseThrow(() -> new IncidentNotFoundException(id));
            case RESCUE_TEAM -> incidentRepository.findByIdForTeam(id, user.subject())
                    .map(this::toRescueResponse).orElseThrow(() -> new IncidentNotFoundException(id));
        };
    }

    public Object updateStatus(long id, IncidentStatusUpdateRequest request, AuthenticatedUser user) {
        if (user.role() == ApiRole.CITIZEN) {
            throw new ForbiddenOperationException("Citizens cannot update incident status");
        }
        if (user.role() == ApiRole.RESCUE_TEAM && incidentRepository.findByIdForTeam(id, user.subject()).isEmpty()) {
            throw new IncidentNotFoundException(id);
        }
        IncidentStatus targetStatus = IncidentStatus.parse(request.status())
                .orElseThrow(() -> new InvalidIncidentStatusException());
        ControlCenterIncidentResponse incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        IncidentStatus currentStatus = IncidentStatus.parse(incident.status())
                .orElseThrow(() -> new IncidentStatusTransitionException(
                        "Incident status cannot be advanced from its current value"));

        if (currentStatus.next().filter(targetStatus::equals).isEmpty()) {
            throw new IncidentStatusTransitionException(
                    "Status can only advance from " + currentStatus + " to its next workflow status");
        }

        boolean updated = user.role() == ApiRole.RESCUE_TEAM
                ? incidentRepository.updateStatusForTeam(id, currentStatus.name(), targetStatus.name(), user.subject())
                : incidentRepository.updateStatus(id, currentStatus.name(), targetStatus.name());
        if (!updated) {
            if (user.role() == ApiRole.RESCUE_TEAM
                    ? incidentRepository.findByIdForTeam(id, user.subject()).isEmpty()
                    : incidentRepository.findById(id).isEmpty()) {
                throw new IncidentNotFoundException(id);
            }
            throw new IncidentStatusTransitionException(
                    "Incident status changed concurrently; reload the incident and try again");
        }
        return findById(id, user);
    }

    public ControlCenterIncidentResponse assignTeam(long id, IncidentAssignmentRequest request, AuthenticatedUser user) {
        requireRole(user, ApiRole.ADMIN);
        ControlCenterIncidentResponse incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        IncidentStatus currentStatus = IncidentStatus.parse(incident.status())
                .orElseThrow(() -> new IncidentStatusTransitionException(
                        "Team assignment is only allowed while incident status is SUBMITTED"));
        if (currentStatus != IncidentStatus.SUBMITTED
                || currentStatus.next().filter(status -> status == IncidentStatus.TEAM_ASSIGNED).isEmpty()) {
            throw new IncidentStatusTransitionException(
                    "Team assignment is only allowed while incident status is SUBMITTED");
        }

        String assignedTeam = request.assignedTeam().trim();
        if (!incidentRepository.assignTeam(id, currentStatus.name(), assignedTeam, IncidentStatus.TEAM_ASSIGNED.name())) {
            if (incidentRepository.findById(id).isEmpty()) {
                throw new IncidentNotFoundException(id);
            }
            throw new IncidentStatusTransitionException(
                    "Incident status changed concurrently; reload the incident and try again");
        }
        return incidentRepository.findById(id).orElseThrow(() -> new IncidentNotFoundException(id));
    }

    private void requireRole(AuthenticatedUser user, ApiRole required) {
        if (user.role() != required) {
            throw new ForbiddenOperationException("This operation is not allowed for the current role");
        }
    }

    private CitizenIncidentResponse toCitizenResponse(ControlCenterIncidentResponse incident) {
        return new CitizenIncidentResponse(incident.id(), incident.incidentType(), incident.description(),
                incident.location(), incident.status());
    }

    private RescueTeamIncidentResponse toRescueResponse(ControlCenterIncidentResponse incident) {
        return new RescueTeamIncidentResponse(incident.id(), incident.incidentType(), incident.description(),
                incident.location(), incident.contactNumber(), incident.status(), incident.severity(),
                incident.affectedPeople(), incident.requiredResources(), incident.assignedTeam());
    }
}

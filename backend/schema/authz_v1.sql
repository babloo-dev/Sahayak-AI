-- Additive ownership mapping for the pre-existing sahayak_ai.incident table.
-- Review and apply manually after confirming incident.id is BIGINT PRIMARY KEY.
-- This script does not alter, rename, or delete incident rows or columns.
CREATE TABLE IF NOT EXISTS incident_owner (
    incident_id BIGINT NOT NULL,
    citizen_subject VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (incident_id),
    INDEX idx_incident_owner_subject (citizen_subject),
    CONSTRAINT fk_incident_owner_incident
        FOREIGN KEY (incident_id) REFERENCES incident (id)
);

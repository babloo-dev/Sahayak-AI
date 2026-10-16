# TASKS.md — Small-Step Implementation Plan

Work from top to bottom. Do not start a later task until the current task's acceptance checks pass. Keep each task small and update `MEMORY.md` truthfully.

## Phase 0 — Workspace
- [x] T001: Inspect the selected workspace and confirm it is a fresh Sahayak-AI project folder; do not overwrite unrelated files.
- [x] T002: Establish the official repository root and ensure all project documentation is in it. The user-designated root already existed and contained all required documents, so no copy or replacement was needed.
- [x] T003: Add `.gitignore` and `.env.example` placeholders; verify no secrets are present.
- [x] T004: Add root README with prerequisites and planned local start commands. Startup commands are explicitly marked as planned and untested.

## Phase 1 — Frontend foundation
- [x] T005: Scaffold React + Vite frontend in `frontend/` using the JavaScript template.
- [x] T006: Implement CSS design tokens and responsive AppShell with a light background, dark navy sidebar, and blue active state.
- [x] T007: Add reusable accessible UI components for loading, empty, error, stale data and status badges.
- [x] T008: Add role-aware navigation and placeholder pages with clearly labelled not-yet-connected states.

## Phase 2 — Backend foundation
- [x] T009: Create Spring Boot backend in `backend/` with a tested health endpoint; document Java 17 baseline, JDK 26.0.2, Maven 3.9.16, and Spring Boot 4.1.1.
- [x] T010: Configure MySQL JDBC connection settings via environment variables; add a separate database health check and safe unavailable response. Automated tests pass; the running application later returned HTTP 200 with database status UP.
- [ ] T011: Add database migration/schema strategy and initial user/role/incident tables.
- [ ] T012: Add DTOs, validation, consistent API errors and API documentation.

## Phase 3 — Identity and incident workflow
- [ ] T013: Implement development authentication with clearly labelled local-only mode; design OTP provider interface for later real integration. A local-only bearer-token strategy is implemented; no OTP delivery or verification is claimed. Local runtime setup remains unverified.
- [ ] T014: Enforce server-side roles and citizen ownership checks. Backend guards, role-specific DTOs, and ownership queries are implemented and tested; the reviewed additive `incident_owner` SQL remains unapplied, so live ownership persistence is not verified.
- [x] T015: Implement incident create/list/detail endpoints and persist records. Repository maps to the existing singular `incident` schema. On 2026-10-16, the live database health check returned connected=true; one approved test-only POST returned 201 with ID 7, and that record was retrieved through both detail and list endpoints. `mvn clean verify` passed (12 tests). No automatic migration is configured; the test record remains in the database.
- [ ] T016: Connect Citizen Report form and My Reports to real APIs. The form submits to the backend; authenticated My Reports now uses owner-filtered endpoints, pending local ownership-table setup and runtime verification.
- [ ] T017: Implement rescue-team records, incident assignment and status history. Incident assignment now uses the existing `assigned_team` column and moves `SUBMITTED` incidents to `TEAM_ASSIGNED`; sequential status updates are implemented. A managed team registry and persisted status history remain incomplete.
- [ ] T018: Connect Rescue Team dashboard and Admin incident management to real APIs. Control Center reads backend incidents; Rescue Team assigned-incident list and next-status action now use role-filtered APIs. Full workflow/runtime verification remains pending.
- [ ] T019: Verify end-to-end reporting → assignment → status update → citizen view.

## Phase 4 — Real data foundation
- [ ] T020: Select one pilot region and inventory real source availability, terms, variables and timestamps.
- [ ] T021: Implement one weather-data connector with timeout, validation, freshness and source metadata.
- [ ] T022: Investigate historical flood labels and matching rainfall/river-level records; document gaps.
- [ ] T023: Create reproducible data-cleaning and feature-building scripts; do not fabricate missing labels.

## Phase 5 — Flood ML capability
- [ ] T024: Define a defensible target and forecast horizon based on available labels.
- [ ] T025: Create leakage-safe train/validation/test split and baseline model.
- [ ] T026: Evaluate model with suitable metrics and record limitations.
- [ ] T027: Save preprocessing + model pipeline and metadata/version.
- [ ] T028: Create Python FastAPI health/readiness and flood-inference endpoints.
- [ ] T029: Verify inference with valid inputs and reject invalid/stale/missing inputs appropriately.
- [ ] T030: Integrate the Python service with Spring Boot and persist prediction metadata.
- [ ] T031: Add Citizen Early Warning UI for flood risk, source, horizon and freshness.

## Phase 6 — Multi-disaster support
- [ ] T032: Add a hazard capability registry so each hazard indicates whether it has ML prediction, official forecast, event monitoring or is unsupported.
- [ ] T033: Add official seismic event/alert integration where accessible; never claim exact earthquake prediction.
- [ ] T034: Add official cyclone and severe-weather forecast/advisory integration where accessible.
- [ ] T035: Investigate landslide data and implement a validated risk model only if suitable data supports it.
- [ ] T036: Add heatwave and drought indicators/official information where suitable sources are verified.
- [ ] T037: Build unified Early Warning UI while keeping model estimates and official alerts separate.

## Phase 7 — Admin, map and hardening
- [ ] T038: Add operational dashboard metrics from real database queries.
- [ ] T039: Add map markers from actual incident coordinates and verified alert locations.
- [ ] T040: Add resource allocation and analytics based on stored records.
- [ ] T041: Add tests for authorization, ownership, incident workflow and data freshness.
- [ ] T042: Write complete startup, configuration and demo instructions.
- [ ] T043: Produce a truthful feature status report: verified, incomplete, unavailable or awaiting credentials/data.

## Current task
T001–T010 and T015 are complete; the live API created and retrieved approved test record ID 7 using the existing singular `incident` table. Incident status advancement, team assignment, Citizen submission form, and Control Center incident list/detail integration are implemented. Local bearer-token authentication and role/ownership enforcement are implemented; latest automated backend verification passes 39 tests, and frontend lint/build checks pass. T013–T014 remain incomplete pending local setup and additive owner-table application/runtime verification. Production OTP is not implemented. Read-only local smoke checks previously confirmed frontend HTTP 200, database health connected=true, and incident-list HTTP 200. The CORS allow-list supports the exact local Vite origins (`localhost` and `127.0.0.1`), and permits the Authorization request header. Runtime CORS/authentication remain unverified; prior port 8080 HTTP checks were refused, and local datasource settings were unavailable in that shell. Browser console/render inspection also remains pending. T011–T012 remain incomplete; T017 still needs a managed rescue-team registry and persisted status history. The current `Sahayak-AI-Final` folder is the official project root. Do not create another `Sahayak-AI` folder or overwrite unrelated files.

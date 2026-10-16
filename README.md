# Sahayak AI

Sahayak AI is a planned, locally runnable disaster-management application for academic demonstration and continued development. It will support incident reporting, rescue response coordination, and access to clearly sourced disaster-risk information and official alerts. It is not a certified emergency-warning system.

This current `Sahayak-AI-Final` folder is the official project root. The implementation is being built incrementally from the specifications in `PROJECT_BRIEF.md`, `RULES.md`, `DESIGN.md`, `FLOW.md`, and `TASKS.md`.

## Roles

- **Citizen/Reporter:** Submit incidents, view the status of their own reports, and view available hazard information and official alerts. Citizens must not see internal operational notes or private rescue-team details.
- **Rescue Team:** View authorized assignments and update response status.
- **Admin/Control Center:** Monitor incidents, assign rescue teams, manage resources, review risk information, and view operational analytics.

The backend must enforce these permissions. Hiding a screen or button is not an authorization check.

## Planned architecture

- `frontend/` — React + Vite using JavaScript, HTML, and CSS. The UI uses an off-white page background, dark navy sidebar with a blue active state, white cards, professional blue actions, and restrained Liquid Glass accents.
- `backend/` — Java + Spring Boot REST API. It exposes health endpoints, role-protected incident workflows, and a local-only bearer-token development authentication strategy. Production OTP authentication, a managed team registry, resources, and media upload remain future work.
- `ml-service/` — Python + FastAPI for model-serving and related data/ML work where suitable, verified data supports it, and evaluation justifies it.
- **MySQL** — source of truth for persistent application records.

The React + Vite frontend and Spring Boot backend include core incident reporting and response workflows. The Python service and risk/alert workflows are still planned. Hazard information must identify its source and timestamps. Official forecasts/alerts must remain separate from model estimates. Unsupported or unavailable predictions must be reported as unavailable, and earthquake information must not claim exact prediction.

## macOS development prerequisites

Install these tools before running the services:

- Java JDK 17 or later (Spring Boot 4.1.1 supports Java 17–26) and Apache Maven 3.6.3 or later for Spring Boot. This backend targets Java 17 bytecode; it was built and run with JDK 26.0.2 and Maven 3.9.16.
- Node.js and npm for React + Vite.
- Python 3 and `pip` for the FastAPI service.
- MySQL Server and its command-line client.
- Git for version control.
- Visual Studio Code (VS Code) for editing the project.

No software installation is performed by this setup guide. Backend build and health-endpoint checks have been run; frontend and Python service startup commands below remain unverified.

## Database and local configuration

The root `.env.example` contains placeholder configuration only. Copy it to a local `.env` and replace the database credentials and local development token placeholders. Generate three distinct long random values for the Citizen, Rescue Team, and Admin tokens. Do not commit `.env`, and never put secrets in `VITE_*` variables because Vite exposes those values to browser code.

After MySQL is installed and running, connect using your local MySQL administrator account:

```sh
mysql -u root -p
```

At the MySQL prompt, create a local development database and a restricted application user. Replace the password placeholder locally; do not put a real password in this README:

```sql
CREATE DATABASE sahayak_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'sahayak_dev'@'localhost' IDENTIFIED BY 'REPLACE_WITH_LOCAL_PASSWORD';
GRANT ALL PRIVILEGES ON sahayak_ai.* TO 'sahayak_dev'@'localhost';
EXIT;
```

Set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` in your local `.env` to match that database and user. Spring Boot reads these values from the process environment; it does not load `.env` automatically. From `backend/`, load the root `.env` before starting the service (the file remains local and ignored by Git):

```sh
set -a
. ../.env
set +a
```

The backend also accepts `SERVER_PORT` (default `8080`) and `SAHAYAK_CORS_ALLOWED_ORIGIN` (default `http://localhost:5173,http://127.0.0.1:5173`). This is a comma-separated allow-list of exact origins for `/api/**`; the `Authorization` header is allowed, credentials and wildcard origins are disabled. For deployment, set only the exact frontend origin(s) needed by that environment; do not use `*`. Do not use these development database instructions for production.

### Local authentication and additive ownership schema

Local development uses three distinct bearer tokens configured in the backend environment: `SAHAYAK_DEV_CITIZEN_TOKEN`, `SAHAYAK_DEV_RESCUE_TOKEN`, and `SAHAYAK_DEV_ADMIN_TOKEN`. The server maps each token to its role; clients cannot select or override a role. The frontend asks for one token in memory and reads the authoritative role from `GET /api/auth/me`. The example `.env` activates the `local` profile and development-token mode; these tokens are disabled unless both the local profile and `SAHAYAK_AUTH_DEV_TOKEN_ENABLED=true` are active. Use unique random values at least 32 characters long, keep them only in the ignored local `.env`, and never put them in `VITE_*` settings. The Rescue Team identity in `SAHAYAK_DEV_RESCUE_TEAM` must exactly match the existing `incident.assigned_team` text for its assigned incidents.

Generate three separate values locally with `openssl rand -hex 32` and place them directly in the ignored root `.env`; do not paste tokens into chat or commit them. Replace every `replace_...` placeholder before starting the backend.

This local strategy stores no passwords and does not verify or send OTPs. Production Citizen OTP requires a real SMS delivery provider and account credentials; options include a configured telecom/SMS gateway or a provider such as Twilio or AWS SNS. No provider is configured, and no OTP is sent or verified by this project. Local bearer tokens are for development only and are not production authentication.

New citizen reports need an owner link. A reviewed additive SQL script is at `backend/schema/authz_v1.sql`; it creates only `incident_owner`, referencing the existing `incident.id`. Review the actual schema and apply this script manually before using citizen report ownership. It does not modify the existing incident table or records. Existing incidents remain unowned and unavailable to citizens; rescue users can see records assigned to their configured team. The script has not been applied to MySQL.

Role access: Citizens may create and read only their own reports and receive a limited response without contact or internal operational fields. Rescue Teams may read only incidents assigned to their server-configured team and may advance those incidents. Admin may list/read all incidents, assign teams, and advance statuses. Other roles receive HTTP 403; inaccessible incident IDs return 404. These access rules have automated tests, but database-backed ownership and live authentication have not yet been verified.

## Local startup commands and verification status

Run each service in its own terminal. The backend commands have been run successfully. Frontend lint and production build are verified. An earlier local smoke attempt returned frontend HTTP 200, database health `connected: true`, and incident-list HTTP 200, but preflight responses lacked the expected CORS header. The CORS source now allows the two exact local Vite origins by default. In the latest check, a Java process was listed on port 8080, but HTTP connections from this shell were refused; no runtime CORS result is currently verified. Restart the backend with the configured datasource variables and current build before relying on browser API calls. Python commands remain planned and untested.

### Spring Boot backend and incident API

```sh
cd backend
set -a
. ../.env
set +a
mvn clean verify
mvn spring-boot:run
```

The backend targets Java 17 and uses Spring Boot 4.1.1. The health endpoint is independent of MySQL and returns `{"service":"Sahayak AI","status":"UP"}` at `http://localhost:8080/api/health`. The Maven build and startup commands were verified with JDK 26; `mvn clean verify` includes the endpoint test. The `.env` loading commands are documented but were not exercised because no local `.env` file exists.

The separate `GET http://localhost:8080/api/health/database` check returns HTTP 200 with `{"service":"Sahayak AI database","status":"UP","connected":true}` when the configured database answers `SELECT 1`, or HTTP 503 with a generic DOWN response when unavailable. It never returns connection details or exception text. Automated tests cover both outcomes without requiring MySQL. On 2026-10-16, the configured running application returned HTTP 200 with `connected: true`.

### Incident API and authorization

The backend provides the following endpoints using the existing singular MySQL table `incident`. It maps `incidentType` to `disaster_type` and uses the existing `description`, `location`, `contact_number`, and `status` columns. It also reads Control Center fields already present in that table: `severity`, `affected_people`, `required_resources`, `priority`, and `assigned_team`. No Flyway migration is configured, so startup will not create a parallel table or migration history.

- `GET /api/auth/me` — validate the bearer token and return the backend-assigned role.
- `POST /api/incidents` — Citizen only; returns a limited receipt with ID and initial status.
- `GET /api/incidents` — role-filtered list; Admin sees all, Citizen sees owned reports, Rescue Team sees assignments. Optional `?status=SUBMITTED` filter applies within each role's allowed records.
- `GET /api/incidents/{id}` — role-checked detail; Citizen fields omit contact and internal operational details.
- `PATCH /api/incidents/{id}/status` — Admin or the assigned Rescue Team may advance the incident.
- `PATCH /api/incidents/{id}/assignment` — Admin only.

Example request body:

```json
{
  "incidentType": "Flood",
  "description": "Water entering homes",
  "location": "Ward 4, Riverside",
  "contactNumber": "+91XXXXXXXXXX"
}
```

The server sets initial `status` to `SUBMITTED`. The existing table has no `reported_at` or `media_reference` column; the API does not accept or return those fields, and it does not invent a report time or discard a media reference. Validation errors return HTTP 400, missing IDs return 404, successful submission returns 201, and storage errors return a generic 503. Missing or invalid bearer tokens return 401, and forbidden role actions return 403. Admin and assigned Rescue Team responses include contact details; Citizen responses do not.

Status updates accept a JSON body such as `{"status":"TEAM_ASSIGNED"}`. Supported sequential transitions are `SUBMITTED` → `TEAM_ASSIGNED` → `TEAM_ON_THE_WAY` → `RESCUE_OPERATION_STARTED` → `RESCUE_COMPLETED`. The endpoint returns the updated Control Center incident response. Unsupported values return HTTP 400; skipped or otherwise invalid transitions return HTTP 409; nonexistent incidents return HTTP 404. The update uses the existing `status` column and checks the previous status in the SQL update to avoid overwriting a concurrent change. No schema change or status history is added.

Team assignment accepts `{"assignedTeam":"North response team"}` and is allowed only while the incident is `SUBMITTED`. It stores the provided team name in the existing `assigned_team` column and advances status to `TEAM_ASSIGNED` in one conditional update. Blank names or names longer than the existing 255-character column return HTTP 400; an incident that has already advanced returns HTTP 409; missing IDs return HTTP 404. There is no rescue-team table or registry, so the API validates the team name's presence and length but cannot verify it against managed team records. No schema change is made.

The safe current schema strategy is to use the existing `incident` table without DDL changes. If report timestamps or media references are needed later, first review an additive sidecar-table migration linked to `incident.id`; do not alter or replace the existing table. No migration has been run. Automated API and SQL-mapping tests cover creation, validation, status filtering, detail retrieval, missing IDs, and use of the singular table. On 2026-10-16, live persistence was verified against the configured database: one explicitly approved test-only record was created (ID 7) and retrieved by both detail and list endpoints. The record remains in the database as test data; no existing records were modified or deleted. At that time, `mvn clean verify` passed 12 tests; the latest verification after status and team-assignment changes passes 23 tests.

### React + Vite frontend

```sh
cd frontend
cp -n .env.example .env
npm install
npm run dev
```

`VITE_API_BASE_URL` defaults to `http://localhost:8080/api` and is public browser configuration, not a place for secrets. The local token login asks for a token without a role selector; the server determines the role. Tokens are kept in memory only and cleared on sign-out or reload. The Citizen form, My Reports, Rescue Team assigned-incidents list/status action, and Control Center list/detail use role-filtered endpoints. Citizen reports require the manual additive ownership script described above. OTP is not available.

### Python FastAPI AI service (planned; not scaffolded or tested)

```sh
cd ml-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

The Python dependency set and service entry point will be finalized in their implementation task. The Python service is not yet configured to use the MySQL database.

## Current implementation status

| Area | Current status |
| --- | --- |
| Implemented | Incident create/list/detail, sequential status updates, team assignment, frontend incident workflows, local bearer-token identity, server-side role guards, and ownership-aware queries. |
| Automated verification | Latest `mvn clean verify` passed 39 tests; latest frontend `npm run lint` and `npm run build` passed. These checks validate code and mocked/test-database behavior, not live owner-schema setup. |
| Live database verification pending | The additive `backend/schema/authz_v1.sql` ownership script has not been applied. Live authentication, citizen ownership persistence, and role-filtered access have not been verified against the configured database. Existing incident persistence was previously verified separately. |
| Not implemented | Production OTP/SMS, managed rescue-team registry, persisted status history, real-data connectors, AI predictions, Early Warning, resource management, and operational analytics. |

Local bearer tokens are a development strategy only; the frontend submits a token and reads the backend-assigned role. Do not treat this as production identity or OTP authentication. Runtime browser-to-backend CORS/auth checks also remain unverified. See `TASKS.md` and `MEMORY.md` for the task-level record.

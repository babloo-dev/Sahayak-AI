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
- `backend/` — Java + Spring Boot REST API. The initial scaffold currently exposes only `GET /api/health`; authentication, authorization, incident workflows, assignments, resources, and persistence are future work.
- `ml-service/` — Python + FastAPI for model-serving and related data/ML work where suitable, verified data supports it, and evaluation justifies it.
- **MySQL** — source of truth for persistent application records.

The React + Vite frontend and the initial Spring Boot backend scaffold exist. The Python service and all application workflows are still planned. Hazard information must identify its source and timestamps. Official forecasts/alerts must remain separate from model estimates. Unsupported or unavailable predictions must be reported as unavailable, and earthquake information must not claim exact prediction.

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

The root `.env.example` contains placeholder configuration only. Copy it to a local `.env` and replace the database username/password placeholders with values you choose for your machine. Do not commit `.env`, and never put secrets in `VITE_*` variables because Vite exposes those values to browser code.

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

The backend also accepts `SERVER_PORT` (default `8080`). Do not use these development instructions for a production database.

## Local startup commands and verification status

Run each service in its own terminal. The backend commands have been run successfully; the frontend and Python commands are planned and have not been tested.

### Spring Boot backend (scaffold and health endpoint verified)

```sh
cd backend
set -a
. ../.env
set +a
mvn clean verify
mvn spring-boot:run
```

The backend targets Java 17 and uses Spring Boot 4.1.1. The health endpoint is independent of MySQL and returns `{"service":"Sahayak AI","status":"UP"}` at `http://localhost:8080/api/health`. The Maven build and startup commands were verified with JDK 26; `mvn clean verify` includes the endpoint test. The `.env` loading commands are documented but were not exercised because no local `.env` file exists.

The separate `GET http://localhost:8080/api/health/database` check returns HTTP 200 with `{"service":"Sahayak AI database","status":"UP","connected":true}` when the configured database answers `SELECT 1`, or HTTP 503 with a generic DOWN response when unavailable. It never returns connection details or exception text. Automated tests cover both outcomes without requiring MySQL. A real MySQL connection has not yet been verified on this machine; `mysqladmin ping` found no local server responding at the default socket.

### React + Vite frontend (planned; startup not tested)

```sh
cd frontend
npm install
npm run dev
```

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

The React + Vite JavaScript frontend in `frontend/` has the light page theme, navy sidebar, blue active navigation, and responsive AppShell. The Spring Boot backend in `backend/` has an application health endpoint and an environment-configured MySQL connectivity check; local database connectivity remains unverified. Role-specific dashboards, incident workflows, warning features, database persistence, authentication, and real-data integrations remain unimplemented. See `TASKS.md` and `MEMORY.md` for tracked progress.

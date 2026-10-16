# FLOW.md — Application and Data Flows

## Citizen incident reporting
1. Citizen signs in.
2. Opens Report an Incident.
3. Enters incident type, description, location and contact; optionally adds media.
4. Frontend validates basic required fields and submits to Spring Boot.
5. Backend validates authorization and data, stores incident and media reference in MySQL/storage.
6. API returns the saved incident ID and status.
7. The incident appears in that citizen's My Reports and in the authorized Control Center.

## Rescue response
1. Admin opens an active incident.
2. Admin assigns an eligible rescue team.
3. Backend persists assignment and initial response status.
4. Rescue Team sees its assigned incident.
5. Team updates status through authorized API calls.
6. Backend stores each status change/history.
7. Citizen sees only the current status of their own report; Admin sees authorized operational details.

## Early Warning request
1. Citizen selects a supported hazard and location.
2. Backend checks whether a valid source/model is configured for that hazard and location.
3. Data connector fetches source observations/forecasts and records source timestamps.
4. Data-quality layer checks missing, stale, malformed or mismatched data.
5. For a supported ML hazard, Python service loads the trained pipeline and returns a risk estimate with model version and horizon.
6. For hazards supported only by official forecasts, display those forecasts as official-source information, not as an Sahayak AI ML prediction.
7. Store the result/metadata where appropriate.
8. React displays AI estimate and official warning in separate sections.
9. If data/model/source is unavailable, show a clear unavailable state; never show a fabricated risk score.

## Official alert flow
1. Retrieve from a verified official source/feed.
2. Preserve issuing authority, source reference, issue time, expiry/effective period and affected area where provided.
3. Show the alert as official only when its provenance can be verified.
4. Do not rewrite an AI estimate as an official alert.

## Roles and data boundaries
- Citizen: own reports and public alerts/risk information only.
- Rescue Team: authorized assignments and necessary response details.
- Admin: operational incident, team, resource and analytics views.
- All permissions are checked in backend endpoints, not just the UI.

# Sahayak AI — Project Brief

## 1. Goal
Build a genuinely working, locally runnable AI-assisted multi-disaster management application for an academic demonstration and continued development. This is not a UI-only prototype and must not be represented as a certified emergency-warning system.

## 2. Core roles
1. **Citizen/Reporter** — report incidents, check supported disaster-risk information, view official alerts, and track only their own reports.
2. **Rescue Team** — view assigned incidents and update response status.
3. **Control Center/Admin** — monitor incidents, assign rescue teams, manage resources, review risk assessments, and view analytics.

Server-side authorization is mandatory; hiding UI controls alone is not security.

## 3. Citizen features
- Phone-number OTP login is the intended production flow; actual OTP requires a configured SMS provider. For local development, a clearly labelled development login may be used until OTP is configured.
- Report incident with type, description, location, contact information, timestamp, and optional photo/video.
- View My Reports with incident details and rescue status only; do not expose admin-only notes or private rescue-team information.
- A prominent **Early Warning / Predict Disaster Risk** section on the Citizen Dashboard.
- Citizen selects a supported hazard and location, then sees available real-data-based risk/forecast information, data freshness, forecast horizon, sources, and official alerts separately.

## 4. Disaster categories and honest capability labels
The interface may list:
- Flood
- Landslide
- Earthquake
- Cyclone
- Severe storm / thunderstorm / lightning / strong wind
- Heatwave
- Drought
- Future extensions such as tsunami, avalanche and forest fire

Do not claim every hazard can be predicted exactly. Different hazards require different data and methods:
- **Flood:** potentially a trained ML risk model using suitable historical flood labels, rainfall, river levels/trends, location and forecast inputs.
- **Landslide:** risk model only where suitable terrain, rainfall, geological and historical event data exist.
- **Earthquake:** display seismic events and official alerts. Do not claim to predict exact time, place or magnitude. Earthquake early warning detects an event already in progress where such systems exist.
- **Cyclone and severe weather:** integrate appropriate official forecasts/advisories unless a separately validated model is justified.
- **Heatwave/drought:** use suitable forecasts, observations and validated indicators/models.
- Unsupported hazard/location or unavailable data must produce a clear unavailable/unsupported state, not a fabricated result.

## 5. Technology
- Frontend: React + Vite, JavaScript, HTML and CSS.
- Backend: Java + Spring Boot REST API.
- Database: MySQL.
- ML/data service: Python, pandas, NumPy, scikit-learn where appropriate, and FastAPI for model-serving.
- Maps: a suitable provider such as OpenStreetMap-based tools, following usage terms and attribution.
- Keep frontend, backend and Python service modular, with documented local run commands.

## 6. Main backend capabilities
- Authentication and role checks.
- Incident create/list/detail operations.
- Citizen-specific report access.
- Rescue-team assignment and status updates.
- Resource allocation and dashboard summaries based on database records.
- Prediction records and official-alert retrieval.
- Health/readiness endpoints and clear errors when external services are unavailable.

## 7. Data source plan
Investigate and verify actual access, coverage, terms and variables before integrating:
- Weather forecast: Open-Meteo documentation, https://open-meteo.com/en/docs
- Historical rainfall: suitable IMD/NWIC data where available, https://nwdp.nwic.gov.in/dataset/rainfall-daily-imd
- River levels/flood forecasts: Central Water Commission / official flood forecast portal, https://aff.india-water.gov.in/
- Earthquake, cyclone and severe-weather information: appropriate official government/meteorological sources.
- Mapping: a compliant mapping provider.

Do not assume an API exposes a variable until verified. Track source, location and observation/forecast timestamp. Never hardcode secrets; use environment variables.

## 8. ML requirements
- Inspect dataset provenance, labels, geography, time resolution and missingness before training.
- Align data by location/time; avoid data leakage.
- Use appropriate time/event-aware train-validation-test splits.
- Establish a baseline and evaluate with appropriate metrics, including recall and false negatives for hazard detection; assess calibration when outputting probabilities.
- Store model version, training data reference, feature timestamp and prediction timestamp.
- Never fabricate accuracy or describe a heuristic as a trained ML model.
- A model estimate is not an official warning and is not a guarantee of safety.

## 9. Visual identity
Use the Sahayak AI light-theme direction: off-white/light grey main background, dark navy left sidebar with white text and a clear blue active state, white rounded cards with subtle shadows, dark navy headings, muted grey secondary text, and professional blue primary actions. Keep the layout spacious, responsive, readable, and accessible. Use Liquid Glass sparingly with minimal blur and preserve readability when effects are unavailable. Do not use another product's branding or green palette.

## 10. Data integrity and security
- MySQL is the source of truth for persistent application records.
- Validate inputs and uploaded file type/size.
- Enforce role-based access on the server.
- Do not expose secrets, private data or internal admin information to citizens.
- Handle timeouts, rate limits, stale/missing data and upstream outages.
- Separate official alerts from model estimates everywhere.
- Mark development/mock data clearly and keep it out of live-prediction labels.

## 11. Definition of done for a working academic demo
- App services start using documented commands.
- React calls the actual Spring Boot APIs.
- MySQL records persist and can be retrieved.
- Citizen incident submission works end-to-end.
- Admin can see and assign incidents.
- Rescue Team can update statuses and changes appear in the right dashboards.
- Early Warning retrieves genuine data from configured sources.
- Any ML output labelled as a prediction comes from a trained/evaluated model with suitable data.
- Unsupported or unavailable hazard/location clearly shows unavailable rather than inventing a risk score.
- Setup instructions, configuration examples and a truthful implemented/incomplete feature report are provided.

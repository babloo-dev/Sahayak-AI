# RULES.md — Coding Agent Rules

## Work style
1. Read all project documentation before making changes.
2. Work in small, reviewable steps. Implement only the next unchecked task in `TASKS.md` unless the user explicitly changes scope.
3. Before editing, inspect the repository and explain the intended files/changes briefly.
4. Do not overwrite or delete user work without asking. This is a fresh project, but still inspect the folder first.
5. After each task, run relevant build, lint, test or smoke checks; report actual results.
6. Update `TASKS.md` and `MEMORY.md` after verified progress. Never mark unverified work complete.
7. If blocked by missing credentials, unavailable data, or a design decision, explain the blocker and implement safe scaffolding only where useful.

## Code quality
- Use clear names, small focused modules, consistent formatting and meaningful error handling.
- Avoid giant components/functions. Prefer small reusable React components and service classes.
- Keep frontend, backend, ML service and database concerns separated.
- Validate request inputs on the backend; never trust frontend-only validation.
- Do not add a new dependency/library without checking whether existing tools can solve the task and explaining why it is needed.
- Keep configuration in environment variables and provide `.env.example` files with placeholders only.
- Never commit secrets, passwords, API keys, tokens, personal data or real credentials.
- Add tests for core logic and API behavior as each feature is implemented.

## TypeScript/Java/Python guidance
- If TypeScript is introduced, use strict mode and avoid `any`; do not introduce TypeScript without documenting the choice because the project brief currently specifies React + JavaScript.
- Java code should follow standard Spring Boot structure, use DTOs for API contracts where appropriate, and avoid exposing entities directly when unsafe.
- Python code should use clear functions, type hints where practical, input validation and reproducible model-training steps.
- Keep model preprocessing consistent between training and inference by saving/loading a pipeline where appropriate.

## Data integrity and disaster safety
- Never fabricate live observations, official warnings, data sources, prediction probabilities, confidence, or model metrics.
- Clearly label any development fixture or mock data.
- Missing/stale inputs must not silently become a low-risk result.
- Distinguish AI risk estimates, official forecasts/advisories and observed events.
- Never claim exact earthquake prediction.
- Do not present this academic application as a certified life-safety system.
- Record source, location and timestamps for external data.
- Validate model data provenance and avoid train/test leakage.

## Security
- Enforce role permissions in backend endpoints.
- Citizens may access only their own reports.
- Rescue teams may access only authorized assignments.
- Admin-only operations must be protected server-side.
- Validate and restrict media uploads.
- Do not log OTPs, passwords, tokens or sensitive personal data.

## UI rules
- Use the Sahayak AI light theme: off-white/light grey main background, navy sidebar with white text and blue active navigation, white cards, navy headings, muted grey secondary text, and professional blue primary actions.
- Use `DESIGN.md` as the single source of truth for colors, typography, spacing and controls.
- Liquid Glass is a restrained accent, not a reason to reduce contrast or readability.
- Keep Citizen, Rescue Team and Admin navigation and permissions distinct.
- Show loading, empty, error, stale-data and unavailable states.

## Completion report
At the end of each task, state:
- What changed
- Files created/modified
- Commands/checks actually run and their results
- Remaining blockers or incomplete work
- The next single task

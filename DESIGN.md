# Sahayak AI Design System

## Design direction

Use a clean, modern light theme for a calm, readable disaster-management and rescue platform. Use an off-white page background, white cards, dark navy headings, muted grey secondary text, and professional blue for primary actions and active navigation. The left sidebar is dark navy with white text and a clearly visible blue active state.

Keep layouts spacious, responsive, and accessible. Use rounded cards and restrained shadows. Liquid Glass may appear as a subtle translucent treatment on selected surfaces, with only a small amount of blur; surfaces must remain readable without blur. Avoid excessive glow, deep embossing, low contrast, and decorative effects that distract from emergency information.

This is Sahayak AI. Do not use FoodRescue AI branding, a green brand palette, or visual language that misrepresents this disaster-management platform.

## Color tokens

Define colors as CSS variables in the frontend global stylesheet and use the variables in components rather than scattering raw colors.

- `--color-bg`: `#F4F7FB` — main page background
- `--color-sidebar`: `#10203A` — navy sidebar
- `--color-sidebar-text`: `#F7F9FD` — sidebar text
- `--color-sidebar-muted`: `#AFC2DF` — sidebar supporting text
- `--color-sidebar-secondary`: `#D5DFEF` — secondary sidebar copy
- `--color-sidebar-accent`: `#A9C7FF` — restrained brand accent on navy
- `--color-sidebar-active`: `#2869E8` — active navigation state with white-text contrast
- `--color-surface`: `#FFFFFF` — cards and panels
- `--color-surface-raised`: `#F9FBFF` — subtle raised surfaces
- `--color-primary`: `#2869E8` — primary actions and links
- `--color-primary-hover`: `#1E57C8` — primary hover state
- `--color-text`: `#17243A` — headings and primary text
- `--color-text-muted`: `#5D6B80` — secondary text
- `--color-border`: `#E1E7F0` — light borders
- `--color-border-strong`: `#C4CFDE` — stronger separators
- `--color-focus`: `#155AC8` — visible keyboard focus
- `--color-success`: `#167A55`
- `--color-success-soft`: `#EAF7F1`
- `--color-warning`: `#8C5B00`
- `--color-warning-soft`: `#FFF4D6`
- `--color-warning-border`: `#ECD18A`
- `--color-danger`: `#B4232F`
- `--color-danger-soft`: `#FDEDEE`
- `--color-info`: `#175EB5`
- `--color-info-soft`: `#EBF3FF`

Risk and status colors must be accompanied by text or icons; never rely on color alone. Validate contrast for text, controls, focus indicators, and statuses.

## Typography

- Use a readable system sans-serif stack; add a web font only if it improves readability and loads reliably.
- Page title: 24–30px on desktop, responsive on smaller screens.
- Section heading: 18–22px.
- Body: 14–16px.
- Metadata: 12–13px with sufficient contrast.
- Use dark navy for headings and muted grey for secondary information.

## Layout

- Desktop: fixed or collapsible navy sidebar with blue active navigation; the light content area uses a clear page header and a consistent card grid.
- Mobile: collapse or stack navigation above content; prevent horizontal overflow and keep controls comfortably tappable.
- Use a spacing scale of 4, 8, 12, 16, 24, and 32px.
- Use 8–12px control radii and 14–20px card radii.
- Keep critical warnings prominent and readable without relying on animation or color.

## Surfaces and effects

- Cards use white surfaces, subtle borders, rounded corners, and soft shadows.
- Use Liquid Glass sparingly on selected navigation or top-bar surfaces with a subtle border and restrained backdrop blur.
- Keep text readable when backdrop blur is unsupported or disabled.
- Use soft raised shadows only for selected controls; do not make controls appear disabled or deeply embossed.
- Provide clear hover, focus, active, and disabled states.

## Application roles

Keep experiences and permissions distinct for:

1. **Citizen/Reporter** — submit incidents, view only their own report status, and access public risk information and official alerts.
2. **Rescue Team** — view authorized assignments and update response status.
3. **Admin/Control Center** — monitor incidents, assign teams, manage resources, review risk information, and view operational analytics.

Role boundaries must be enforced by backend authorization, not only by navigation visibility.

## Components

- `AppShell`: sidebar, top bar, and responsive main content area.
- `SidebarNav`: role-aware navigation with a clear blue active state.
- `PageHeader`: title, supporting text, and optional actions.
- `StatCard`: label, value, and trend/source metadata.
- `StatusBadge`: status text plus an icon or other non-color cue.
- `RiskCard`: hazard, location, risk level, horizon, data timestamp, source, and limitation note.
- `OfficialAlertCard`: issuing authority, warning text, affected area, issue/expiry times, and source link when available.
- `DataFreshness`: last update and stale/unavailable state.
- `EmptyState`, `LoadingState`, and `ErrorState`.

## Citizen dashboard priorities

- Make **Report an Incident** prominent.
- Keep **Early Warning / Predict Disaster Risk** prominent and easy to find.
- Show My Reports and the current rescue stage without exposing admin-only notes or private rescue-team information.
- Let citizens select a supported hazard and location, then show availability, data freshness, forecast horizon, and sources.
- Label Sahayak AI estimates separately from official forecasts and alerts. For unsupported hazards or unavailable data, show a clear unavailable state rather than a fabricated risk result.
- For earthquakes, show seismic events and official alerts; never claim exact future prediction.

## Accessibility and safety

- Use semantic HTML, accessible names, and visible keyboard focus.
- Maintain sufficient contrast on light surfaces and in the navy sidebar.
- Buttons must perform real actions and show clear interaction states.
- Do not rely on color, blur, animation, or motion to convey critical information.
- Respect reduced-motion preferences.

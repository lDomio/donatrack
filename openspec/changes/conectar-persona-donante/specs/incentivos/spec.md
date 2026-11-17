## Purpose

Shows the donor's real completed missions and earned badges from incentivos-service combined with illustrative content for what the backend does not expose yet, keeping a full fallback when the service is unreachable.

## ADDED Requirements

### Requirement: Incentives view with merged real content

The system SHALL serve `/personaDonante/incentivos` for an identified donor sourced from `GET /incentivos/{id}/misiones` (completed missions) and `GET /incentivos/{id}/insignias` (earned badges), where `{id}` is the same `idDonante` as `donaciones-service`; real missions and badges SHALL be combined with the illustrative content for whatever the backend does not provide (donor category, category progress, in-progress/blocked missions, locked badge catalog), since `incentivos-service` exposes no mission catalog with progress and `estadoGeneral` (donor category source) is an empty stub.

#### Scenario: Donor with real missions and badges opens incentives

- **WHEN** an identified donor with completed missions and earned badges navigates to `/personaDonante/incentivos`
- **THEN** the system renders the real completed missions and earned badges together with the illustrative categories, progress and locked items, with obtained/total counts

#### Scenario: Donor has no missions or badges yet

- **WHEN** both backend lists are empty for the donor
- **THEN** the view renders the full illustrative content and completes without error

#### Scenario: Incentivos service unreachable

- **WHEN** the service queries fail or time out
- **THEN** the view renders the full illustrative fallback content with a retry message and completes without error

### Requirement: Guest handling for incentives

The system SHALL redirect guests (`donanteIdActual == null`) navigating to `/personaDonante/incentivos` to `/login`, since missions and badges are personal.

#### Scenario: Guest opens incentives

- **WHEN** a non-identified visitor navigates to `/personaDonante/incentivos`
- **THEN** the browser navigates to `/login` instead of rendering the view

## REMOVED Requirements

### Requirement: Incentivos spike

- **Reason**: Spike consumed during implementation: the contract is `GET /incentivos/{id}/misiones` -> completed `MisionResponse(insignia, fecha)` only, `GET /incentivos/{id}/insignias` -> earned `InsigniaResponse(descripcion)` only, `GET /incentivos/{id}/metricas` carries nothing useful for this view (ranking, history, month comparison), and `GET /incentivos/{id}/estadoGeneral` is an empty stub. Full mapping is documented in `plan.md` section V4 and `tasks.md` section 4.
- **Migration**: If the backend later adds a mission catalog with progress and donor category (`estadoGeneral`), reintroduce as a new capability to connect them.

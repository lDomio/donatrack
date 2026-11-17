## Purpose

Shows the donor the decorative donation map with illustrative content while the personal map waits for a route-by-donor endpoint in logistica-service, and defines the spike that will connect it.

## ADDED Requirements

### Requirement: Illustrative map for all visitors

The system SHALL serve `/personaDonante/mapaDonaciones` to guests and identified donors alike with the existing mock content (static image, fixed-position pins, nearby points), explicitly marked as illustrative including the estimated times, without requiring authentication and without real route data. Nearby points SHALL NOT be derived from entities (no address or schedule data exists for `PuntoCercanoDto`).

#### Scenario: Visitor opens the map

- **WHEN** a visitor navigates to `/personaDonante/mapaDonaciones`
- **THEN** the system renders the illustrative map with pins and nearby points and completes rendering without error

#### Scenario: No route data exists

- **WHEN** no personal route exists for the visitor
- **THEN** the map renders the illustrative points and no personal "donacion en ruta" card is emphasized

### Requirement: Personal route card (conditional on missing endpoint)

The system SHALL render the "donacion en ruta" card with real state and estimated time only when `logistica-service` exposes a route query for the donor's donations; until then it SHALL keep the illustrative card. `logistica-service` currently exposes only `GET /logistica-service/rutas`, `/rutas/{id}`, `/entregas/pendientes`, `/entregas/{id}` — no donor query.

#### Scenario: Logistica exposes the donor's route

- **WHEN** an identified donor with an active delivery opens the map and the missing endpoint exists
- **THEN** the route card shows the real donation state and the estimated time from the service

#### Scenario: Logistica donor query unavailable

- **WHEN** no route-by-donor query exists
- **THEN** the card keeps illustrative content and the page still renders without error

### Requirement: Logistica spike

The system SHALL deliver, as the spike output, the endpoint-to-DTO table (`PinMapaDto` / `PuntoCercanoDto`: endpoint, donor or donation filter, estimated-time source) plus the proposal for the missing logistica endpoint, and decide fixed pins (max 3) versus a dynamic list, before connecting the personal map. Note: `auth.service.url` and `logistica.service.url` both point to `localhost:8083` in `application.yaml` and must be reviewed on connection.

#### Scenario: Spike completes

- **WHEN** the spike table and the missing-endpoint proposal are recorded in `plan.md`
- **THEN** the follow-up connection can be specified without further discovery

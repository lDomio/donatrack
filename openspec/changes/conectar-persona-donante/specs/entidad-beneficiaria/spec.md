## Purpose

Lets any visitor browse beneficiary entities and their needs with real backend data in guest mode, and routes the donation intent to login, so the public window works without authentication.

## ADDED Requirements

### Requirement: Public entity browser backed by backend

The system SHALL serve the entity browser at `/personaDonante/entidadesBeneficiarias` to guests and identified donors alike, sourced from `GET /donaciones-service/entidades-beneficiarias`, rendering one card per entity with name, location, description, metrics, progress and impact.

#### Scenario: Guest opens the entity browser

- **WHEN** a non-identified visitor navigates to `/personaDonante/entidadesBeneficiarias`
- **THEN** the system renders the entity cards from the backend response with the page title and shared navigation, without requiring authentication

#### Scenario: Backend is unreachable

- **WHEN** the backend request fails or times out
- **THEN** the system renders the page with an empty entity list and a retry message, without template errors

#### Scenario: Entity has no image

- **WHEN** an entity supplies no image reference
- **THEN** its card renders without an image element and the remaining card content stays intact

### Requirement: Location and description fallback

The system SHALL render each entity's location and description from the backend when present; when `EntidadBeneficiariaResponse` does not yet expose `direccion`/`descripcion`, the system SHALL omit the location line (no fallback from `MedioDeContactoDTO`, which is only `(tipo, valor)`), render the description as `"Descripcion pendiente"`, and render no image element, until the backend adds those fields.

#### Scenario: Backend exposes no direccion

- **WHEN** an entity response carries no address fields
- **THEN** its card renders without a location line, without breaking layout and without placeholder lorem text

### Requirement: Entity impact fallback

The system SHALL render each entity card's impact figure as `0` with an icon resolved from a fixed table by entity `tipo` (default `fa-hand-holding-heart`), until the backend exposes impact data.

#### Scenario: Backend exposes no impact

- **WHEN** an entity response carries no impact figure
- **THEN** its card renders the zero figure with the mapped icon and the remaining card content stays intact

### Requirement: Entity detail backed by backend

The system SHALL serve the entity detail at `/personaDonante/verNecesidadEntidad/{id}` sourced from `GET /donaciones-service/entidades-beneficiarias/{id}`, rendering the entity header, need categories grouped by `tipoNecesidad` (`RECURRENTE` / `EXTRAORDINARIA`, with fixed title, description, icon and color per group), and insumos with progress bars, urgency flags, completion flags and bar colors derived from `NecesidadDto` (`subcategoria` as name, `cantidad`, `cantidadDonada`); `verificada` SHALL render as `false` and the impact figures block SHALL render empty without leaving a hollow block.

#### Scenario: Guest opens an entity's needs

- **WHEN** a visitor navigates to `/personaDonante/verNecesidadEntidad/{id}` with a valid identifier
- **THEN** the browser shows that entity's needs grouped by need type with insumos, progress and urgency

#### Scenario: Entity does not exist

- **WHEN** the backend returns 404 for the requested identifier
- **THEN** the system renders a not-found message with a link back to `/personaDonante/entidadesBeneficiarias`

#### Scenario: Need category has recurrence

- **WHEN** a need carries `periodoRecurrencia P1M` or `P1Y`
- **THEN** its category renders the frequency label "Mensual" or "Anual"; any other value renders no frequency label

#### Scenario: Insumo is fully donated

- **WHEN** a need's `cantidadDonada` reaches or exceeds `cantidad`
- **THEN** its insumo renders with the completed treatment and the green bar color; urgent and normal insumos render red and teal respectively

### Requirement: Donation intent routes to login for guests

The system SHALL route the "Donar ahora" control on entity cards and the "Donar a esta categoria" control on the detail view to `/login` when the visitor is not identified (driven by the `esInvitado` model attribute; templates SHALL NOT read the session), preserving the existing CSS classes. For identified donors the "Donar ahora" control SHALL open the informational needs modal as today, and the category control SHALL open the donation form, which does not submit yet (wired in the POST change).

#### Scenario: Guest activates Donar ahora

- **WHEN** a guest activates the direct-donation control on an entity card
- **THEN** the browser navigates to `/login` instead of opening the needs modal

#### Scenario: Guest activates Donar a esta categoria

- **WHEN** a guest activates the category donation control on the detail view
- **THEN** the browser navigates to `/login` instead of opening the donation form

#### Scenario: Identified donor activates Donar ahora

- **WHEN** an identified donor activates the direct-donation control on an entity card
- **THEN** that entity's informational needs modal becomes visible and the page URL fragment changes to that modal's identifier

### Requirement: Metrics and progress derived from necesidades

The system SHALL derive one `MetricaVistaDto` per `NecesidadDto` (name = `subcategoria`, required/donated from `cantidad`/`cantidadDonada` as integers, percentage with divide-by-zero and null guards, `critica` when coverage is below 30% or `tipoNecesidad` is `EXTRAORDINARIA`); the card progress SHALL be the weighted average (0 when there are no needs); `claseColorProgreso` SHALL be `"alerta"` when any metric is critical, else empty; and the modal need summaries SHALL list critical needs first with `"alerta"` color when urgent.

#### Scenario: A metric has no progress recorded

- **WHEN** a need has zero `cantidadDonada`
- **THEN** its bar renders with an empty fill rather than being omitted

#### Scenario: Required quantity is zero

- **WHEN** a need has zero or null `cantidad`
- **THEN** its percentage renders guarded (no division error) and the card still renders

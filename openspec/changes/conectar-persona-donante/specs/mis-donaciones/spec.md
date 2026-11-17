## Purpose

Presents the logged-in donor's own donations backed by the backend, so the personal zone shows real data instead of mocks. Registering a new donation is out of scope for this change (separate change).

## ADDED Requirements

### Requirement: Personal donation list route (logged-in)

The system SHALL serve the personal donation list at `/personaDonante/misDonaciones` for a donor identified via `donanteIdActual` (session or `app.donante-demo-id`), sourced from `GET /donaciones-service/donaciones/donante/{idDonante}`.

#### Scenario: Logged-in donor opens their donations

- **WHEN** an identified donor navigates to `/personaDonante/misDonaciones`
- **THEN** the system renders one card per donation returned by the backend for that donor, in the order supplied, with title, category, subcategory, detail, state label and image when provided

#### Scenario: Donor has no donations

- **WHEN** the backend returns an empty list for that donor
- **THEN** no donation card is rendered and the page completes rendering without error

#### Scenario: Guest opens the personal list

- **WHEN** a non-identified visitor (`donanteIdActual == null`) navigates to `/personaDonante/misDonaciones`
- **THEN** the system redirects to `/login` instead of rendering the list

#### Scenario: Backend is unreachable

- **WHEN** the backend request fails or times out
- **THEN** the system renders the page with an empty donation list and a retry message, without template errors

### Requirement: Donation card content mapping

The system SHALL derive each card from `DonacionResponse` with the title chain `bienes[0].cantidad + " " + bienes[0].nombre` -> `descripcionGeneral` -> `"Donacion #" + id`; category from `subcategoria.categoriaNombre` and subcategory from `subcategoria.nombre`, both falling back to `"Sin categoria"` when `subcategoria` is `null`; detail from `descripcionGeneral`; state label from the `TipoDeEstado` table; `imagenUrl` always absent (`BienResponse` carries no photo); and `destacado` only when state is `EN_TRASLADO`.

#### Scenario: Donation carries bienes

- **WHEN** a donation has at least one bien with nombre and cantidad
- **THEN** the card title combines them (e.g. "3 camperas de abrigo")

#### Scenario: Donation has neither bienes nor description

- **WHEN** a donation has no bienes and a blank `descripcionGeneral`
- **THEN** the card title is `"Donacion #" + id`

#### Scenario: Donation has no subcategoria

- **WHEN** a donation response carries a `null` subcategoria
- **THEN** both category and subcategory render as `"Sin categoria"`

#### Scenario: Donation has no image reference

- **WHEN** a donation is rendered
- **THEN** no image element is rendered for that donation

### Requirement: State presentation from backend enum

The system SHALL present each donation's state as a visible label from the fixed table (`DEPOSITO`->"En deposito", `ASIGNACION_REALIZADA`->"Asignada", `LISTA_PARA_ENTREGAR`->"Lista para entregar", `EN_TRASLADO`->"En transporte", `ENTREGADA`->"Entregada", `ENTREGA_FALLIDA`->"Entrega fallida", `VENCIDO`->"Vencida"), distinguishing in-route (`EN_TRASLADO`, `LISTA_PARA_ENTREGAR`, `ASIGNACION_REALIZADA`) and delivered (`ENTREGADA`) with visual emphasis, and rendering the remaining states legible without emphasis.

#### Scenario: Donation is in transit

- **WHEN** a donation state maps to in-route
- **THEN** its state label is rendered with the in-transit emphasis

#### Scenario: Donation was delivered

- **WHEN** a donation state is `ENTREGADA`
- **THEN** its state label is rendered with the delivered emphasis

#### Scenario: Donation is in a state with no emphasis

- **WHEN** a donation state is `DEPOSITO`, `VENCIDO` or `ENTREGA_FALLIDA`
- **THEN** its state label is rendered without emphasis and the card remains fully readable

### Requirement: Map shortcut on highlighted donations

The system SHALL render a "Ver mapa" control linking to `/personaDonante/mapaDonaciones` only on cards whose donation is `destacado` (state `EN_TRASLADO`).

#### Scenario: Donation is in transit

- **WHEN** a donation card is rendered with state `EN_TRASLADO`
- **THEN** the card exposes the "Ver mapa" control pointing at the map view for that donation

#### Scenario: Donation is not in transit

- **WHEN** a donation card is rendered with any other state
- **THEN** no "Ver mapa" control is rendered on that card

### Requirement: Filters stay presentational in this change

The system SHALL render the state filter populated from the fixed 7-label table (independent of the loaded data), and the category and subcategory filters populated from the distinct values of the loaded donations, and SHALL NOT be required to filter the list in this change.

#### Scenario: Donations loaded

- **WHEN** the view renders with donations from the backend
- **THEN** the state filter shows the fixed labels and the category/subcategory filters show the distinct loaded values

## REMOVED Requirements

### Requirement: Create donation (logged-in)

- **Reason**: Moved to its own change (see `plan.md` section 5): the form carries `cantidad`/`mensaje`/`contacto` but `BienRequest` requires `nombre`, `unidadDeMedida`, `subcategoriaId` and related fields; neither `NecesidadDto` nor `InsumoNecesidadDto` expose `subcategoriaId`; `CrearDonacionRequest` carries no destination entity or need (matching is done by the matcheador); and the endpoint answers `List<DonacionResponse>`. Wiring needs backend fields, a per-insumo form with hidden fields, and a `@PostMapping` + PRG.
- **Migration**: Reintroduce as a new capability (GET-by-donor stays; POST is added back) once the backend contract and form exist.

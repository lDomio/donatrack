## Purpose

Lets the donor browse the beneficiary entities they can support, compare them on impact and outstanding needs, and start a donation from an entity card or from its detail modal.

## ADDED Requirements

### Requirement: Entity list route

The system SHALL serve the entity browser at `/personaDonante/entidadesBeneficiarias`.

#### Scenario: Donor opens the entity browser

- **WHEN** a donor navigates to `/personaDonante/entidadesBeneficiarias`
- **THEN** the system renders the entity browser with the page title and the shared navigation

### Requirement: Impact summary

The system SHALL render a summary of the donor's aggregate impact, exposing an icon, a numeric figure, and a label.

#### Scenario: Impact figures are available

- **WHEN** the view is rendered with impact figures
- **THEN** the summary shows the supplied figure, icon and label

### Requirement: Entity list filters

The system SHALL render a free-text search field, a type selector, and a set of quick-filter chips, populated from the entity types available to the view.

#### Scenario: Entity types are available

- **WHEN** the view is rendered with entity types
- **THEN** the type selector and the quick-filter chips are both populated, and exactly one quick filter is marked as active

#### Scenario: No entity types are available

- **WHEN** the view is rendered with no entity types
- **THEN** the search field, the type selector and the quick-filter area are still rendered and the page completes rendering without error

### Requirement: Entity cards

The system SHALL render one card per entity, exposing the entity name, its location, its description, its outstanding metrics, its progress, and its impact figure.

#### Scenario: Several entities are available

- **WHEN** the view is rendered with more than one entity
- **THEN** one card appears per entity, in the order supplied, and no entity is omitted or repeated

#### Scenario: An entity has no image

- **WHEN** an entity supplies no image reference
- **THEN** its card renders without an image element and the remaining card content stays intact

### Requirement: Entity type badge

The system SHALL render a type badge on every entity card, and SHALL give each of the four supported types its own distinct badge treatment.

#### Scenario: Entity type is recognised

- **WHEN** an entity's type is one of the four supported types
- **THEN** its badge uses the treatment defined for that specific type

#### Scenario: Entity type is not recognised

- **WHEN** an entity's type is not one of the four supported types
- **THEN** its badge is rendered in the default treatment and the card remains fully readable

### Requirement: Outstanding metrics and progress

The system SHALL render each entity's outstanding metrics, flagging as critical any metric that the view marks critical, and SHALL render each metric's fill as a proportion of its target.

#### Scenario: A metric is critical

- **WHEN** a metric is marked critical
- **THEN** it is rendered with the critical treatment

#### Scenario: A metric has no progress

- **WHEN** a metric has no progress recorded
- **THEN** its bar renders with an empty fill rather than being omitted

### Requirement: Navigate to an entity's needs

The system SHALL render a control on every entity card that navigates to that entity's needs at `/personaDonante/verNecesidadEntidad/{id}`, carrying the entity's identifier.

#### Scenario: Donor opens an entity's needs

- **WHEN** the donor activates the needs control on an entity card
- **THEN** the browser navigates to `/personaDonante/verNecesidadEntidad/{id}` with that entity's identifier in the path

### Requirement: Entity needs modal

The system SHALL allow the donor to open, without scripting, a modal listing the needs of a given entity, and SHALL close it again without scripting. The modal SHALL expose each need's name, its progress, whether it is urgent, and a control to donate.

#### Scenario: Donor opens a needs modal

- **WHEN** the donor activates the direct-donation control on an entity card
- **THEN** that entity's needs modal becomes visible and the page URL fragment changes to that modal's identifier

#### Scenario: Donor closes a needs modal

- **WHEN** the donor activates the modal's close control
- **THEN** the modal becomes hidden and the page URL fragment no longer identifies it

#### Scenario: Several entities are listed

- **WHEN** the view is rendered with more than one entity
- **THEN** each entity's modal is addressable independently, and opening one modal does not open another

#### Scenario: An entity has no needs

- **WHEN** a donor opens the needs modal of an entity that has no needs
- **THEN** the modal renders with an empty needs list and no need rows, without error

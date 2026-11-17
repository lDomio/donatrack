## Purpose

Presents one beneficiary entity in detail, organises its outstanding needs into categories with per-item progress, and lets the donor start a donation towards a category.

## ADDED Requirements

### Requirement: Entity needs route

The system SHALL serve an entity's needs at `/personaDonante/verNecesidadEntidad/{id}`, where the path segment identifies the entity.

#### Scenario: Donor opens an entity's needs

- **WHEN** a donor navigates to `/personaDonante/verNecesidadEntidad/{id}`
- **THEN** the system renders the needs of the entity identified by that path segment

#### Scenario: The route is requested without an identifier

- **WHEN** a donor navigates to `/personaDonante/verNecesidadEntidad` with no identifier
- **THEN** the system does not serve a needs detail page, and the request does not resolve to a rendered needs view

### Requirement: Return to the entity browser

The system SHALL render a control that navigates back to `/personaDonante/entidadesBeneficiarias`.

#### Scenario: Donor leaves the detail for the browser

- **WHEN** the donor activates the back control
- **THEN** the browser navigates to `/personaDonante/entidadesBeneficiarias`

### Requirement: Entity header

The system SHALL render a header identifying the entity, exposing its image, its verified marker when it is verified, its type, its name, its location and its description.

#### Scenario: The entity is verified

- **WHEN** the entity is marked as verified
- **THEN** the verified marker is rendered

#### Scenario: The entity is not verified

- **WHEN** the entity is not marked as verified
- **THEN** the verified marker is omitted and the header remains complete

### Requirement: Entity impact summary

The system SHALL render a summary of the entity's impact, exposing one item per declared impact figure.

#### Scenario: Several impact figures exist

- **WHEN** the entity declares more than one impact figure
- **THEN** one summary item is rendered per figure, and no figure is omitted or repeated

### Requirement: Needs organised by category

The system SHALL organise the entity's needs into categories and render one card per category, exposing the category title, its description, its recurrence label when it has one, and its list of requested items.

#### Scenario: Several categories exist

- **WHEN** the entity has more than one category of needs
- **THEN** one category card is rendered per category, in the order supplied

#### Scenario: A category has no recurrence

- **WHEN** a category has no recurrence
- **THEN** its recurrence label is omitted and the rest of the card is unaffected

### Requirement: Requested item progress

The system SHALL render, for each requested item, its name, its donated and required quantities as text, and a bar filled in proportion to the amount collected so far.

#### Scenario: An item is partially collected

- **WHEN** an item has some but not all of its required quantity
- **THEN** the bar is filled in proportion to the collected amount and the quantities are shown as collected over required

#### Scenario: An item is fully collected

- **WHEN** an item's collected quantity reaches its required quantity
- **THEN** the item is rendered with the completed treatment and its bar is fully filled

#### Scenario: An item has no quantity recorded

- **WHEN** an item has no recorded quantity
- **THEN** the bar renders empty rather than being omitted, and the item stays readable

#### Scenario: An item is urgent

- **WHEN** an item is marked urgent
- **THEN** it is rendered with the urgent treatment

### Requirement: Donation form modal

The system SHALL allow the donor to open, without scripting, a donation form for a given needs category, and SHALL close it again without scripting. The form SHALL expose the category being donated to, labelled input fields, and a confirm control.

#### Scenario: Donor opens the donation form

- **WHEN** the donor activates the donate control on a category card
- **THEN** that category's donation form becomes visible, identifies the category, and the page URL fragment changes to that form's identifier

#### Scenario: Donor closes the donation form

- **WHEN** the donor activates the form's close control
- **THEN** the form becomes hidden and the page URL fragment no longer identifies it

#### Scenario: Several categories are listed

- **WHEN** the entity has more than one category
- **THEN** each category's form is addressable independently, and opening one form does not open another

#### Scenario: Donor activates the confirm control

- **WHEN** the donor activates the form's confirm control
- **THEN** the view does not submit the donation to a donation service, because no donation service is connected

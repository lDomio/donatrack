## Purpose

Presents the donations made by the donor, so they can review what they gave, to whom, in which state, and when.

## ADDED Requirements

### Requirement: Donation list route

The system SHALL serve the donation list at `/personaDonante/misDonaciones`.

#### Scenario: Donor opens the donation list

- **WHEN** a donor navigates to `/personaDonante/misDonaciones`
- **THEN** the system renders the donation list view with the page title and the shared navigation

### Requirement: Donation cards

The system SHALL render one card per donation, and each card SHALL expose the donated item description, the category, the subcategory, the free-text detail, the registration date, and the current state.

#### Scenario: Several donations are available

- **WHEN** the view is rendered with more than one donation
- **THEN** one card appears per donation, in the order supplied, and no donation is omitted or repeated

#### Scenario: A donation has no free-text detail

- **WHEN** a donation has an empty detail field
- **THEN** its card is still rendered and the empty detail does not produce placeholder text

### Requirement: State presentation

The system SHALL present each donation's state as a visible label, and SHALL distinguish in-route and delivered donations from the remaining states by visual emphasis.

#### Scenario: A donation is in transit

- **WHEN** a donation is in the in-transit state
- **THEN** its state label is rendered with the in-transit emphasis

#### Scenario: A donation was delivered

- **WHEN** a donation is in the delivered state
- **THEN** its state label is rendered with the delivered emphasis

#### Scenario: A donation is in a state with no emphasis

- **WHEN** a donation is in a state other than in-transit or delivered
- **THEN** its state label is rendered without emphasis and the card remains fully readable

### Requirement: Donation image is optional

The system SHALL render a donation image when the donation provides one, and SHALL omit the image element entirely when it does not.

#### Scenario: Donation carries an image reference

- **WHEN** a donation provides a non-empty image reference
- **THEN** an image element is rendered for that donation

#### Scenario: Donation has no image reference

- **WHEN** a donation provides an empty image reference
- **THEN** no image element is rendered for that donation

### Requirement: List filters are present

The system SHALL render a state filter, a category filter, and a subcategory filter, populated from the states, categories, and subcategories available to the view.

#### Scenario: No data is available to filter on

- **WHEN** the view is rendered with no donations, states, categories, or subcategories
- **THEN** all three filters are still rendered and the page completes rendering without error

### Requirement: Empty data does not break the view

The system SHALL render the view successfully when there are no donations, and SHALL NOT emit template errors for empty collections.

#### Scenario: No donations exist

- **WHEN** the view is rendered with an empty donation collection
- **THEN** no donation card is rendered and the page completes rendering without error

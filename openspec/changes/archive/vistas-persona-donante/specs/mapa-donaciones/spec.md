## Purpose

Gives the donor a spatial overview of their in-transit donations, a filterable map of collection points, and a way to reach a donation from the entity that needs it.

## ADDED Requirements

### Requirement: Map route

The system SHALL serve the donation map at `/personaDonante/mapaDonaciones`.

#### Scenario: Donor opens the map

- **WHEN** a donor navigates to `/personaDonante/mapaDonaciones`
- **THEN** the system renders the map view with the page title and the shared navigation

### Requirement: Return to the donation list

The system SHALL render a control on the map view that navigates back to `/personaDonante/misDonaciones`.

#### Scenario: Donor leaves the map for the list

- **WHEN** the donor activates the back control
- **THEN** the browser navigates to `/personaDonante/misDonaciones`

### Requirement: In-transit donation summary

The system SHALL render a summary of the donation currently in transit, exposing the donation title, the route detail, the state, and the estimated time.

#### Scenario: A donation is in transit

- **WHEN** the view is rendered with a donation in the in-transit state
- **THEN** the summary card shows that donation's title, route detail, state label and estimated time

#### Scenario: No donation is in transit

- **WHEN** the view is rendered with no donation in the in-transit state
- **THEN** the summary card area is rendered without an in-transit donation

### Requirement: Map filter bar

The system SHALL render a location search field and a set of point-type filter chips.

#### Scenario: Point types are available

- **WHEN** the view is rendered with point types
- **THEN** one filter chip appears per point type, and exactly one chip is marked as the active filter

### Requirement: Map pins

The system SHALL render a map image overlaid with one pin per point, where each pin's position is taken from the point data, and each pin SHALL be visually distinct according to the point's role in the route.

#### Scenario: Route points are available

- **WHEN** the view is rendered with points
- **THEN** one pin appears per point, each at the position declared by its data

#### Scenario: Two points declare the same position role

- **WHEN** more than one point uses the same position role
- **THEN** all of them are still rendered, and the map's declared positions for that role do not change

### Requirement: Pin details on interaction

The system SHALL reveal each pin's detail bubble without scripting, and that bubble SHALL state the point name, its state, the estimated time when one applies, and a link onward.

#### Scenario: Donor points at a pin

- **WHEN** the donor hovers or focuses a pin
- **THEN** the pin's detail bubble becomes visible with the point name, state and, when applicable, the estimated time

### Requirement: Nearby points grid

The system SHALL render the collection points as a responsive grid, exposing each point's name, address, opening hours, and a state label.

#### Scenario: A point is highlighted

- **WHEN** a point is flagged as highlighted
- **THEN** its card is rendered with the highlighted treatment and appears before the non-highlighted points

#### Scenario: Several points are available

- **WHEN** the view is rendered with more than one point
- **THEN** one card appears per point and no point is omitted or repeated

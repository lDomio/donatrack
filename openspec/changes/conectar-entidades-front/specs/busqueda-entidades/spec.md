## Purpose

Permite buscar entidades con el único campo de texto del browser ("Buscar por nombre o ubicación"), matcheando en nombre o ubicación indistintamente, sin partir el form en dos campos.

## ADDED Requirements

### Requirement: Query param busqueda con semántica OR

The system SHALL accept the optional query param `busqueda` on `GET /donaciones-service/entidades-beneficiarias`. When present and non-blank, it SHALL match entities whose `razonSocial` OR `direccion.calle` OR `direccion.ciudad` contains the text (case-insensitive, accent-insensitive), combined in AND with the `tipo` filter. Absent or blank SHALL mean no filtering on this axis. `nombre`/`ubicacion` params are preserved as fine-grained filters.

#### Scenario: Búsqueda por nombre vía busqueda

- **WHEN** un cliente pide `GET /entidades-beneficiarias?busqueda=bajitos`
- **THEN** el sistema devuelve "Comedor Los Bajitos" aunque su ubicación no contenga ese texto, con 200

#### Scenario: Búsqueda por ubicación vía busqueda

- **WHEN** un cliente pide `GET /entidades-beneficiarias?busqueda=siempreviva`
- **THEN** el sistema devuelve las entidades de esa calle aunque su razón social no contenga ese texto, con 200

#### Scenario: busqueda combinada con tipo

- **WHEN** un cliente pide `GET /entidades-beneficiarias?busqueda=vida&tipo=Comedores`
- **THEN** el sistema devuelve solo entidades que matchean "vida" (nombre o ubicación) Y son de tipo COMEDOR, con 200

#### Scenario: busqueda ausente preserva comportamiento

- **WHEN** un cliente pide `GET /entidades-beneficiarias` o solo con `tipo`
- **THEN** el sistema se comporta exactamente como antes de este change, con 200

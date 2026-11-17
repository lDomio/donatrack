## Purpose

Permite filtrar el listado de entidades beneficiarias por nombre, ubicación y tipo desde el backend, para que el browser de entidades deje de filtrar en memoria o de ignorar los filtros del form.

## ADDED Requirements

### Requirement: Listado de entidades con filtros opcionales

The system SHALL extend `GET /donaciones-service/entidades-beneficiarias` with optional query params `nombre`, `ubicacion`, `tipo`, combined with AND semantics. An absent, blank, or (for `tipo`) `"Todas"` value SHALL mean no filtering on that axis. With no params the system SHALL preserve current behavior (return all entities as `List<EntidadBeneficiariaResponse>` with HTTP 200).

#### Scenario: Sin params

- **WHEN** un cliente pide `GET /entidades-beneficiarias` sin query params
- **THEN** el sistema devuelve todas las entidades con 200, idéntico al comportamiento actual

#### Scenario: Filtro por nombre

- **WHEN** un cliente pide `GET /entidades-beneficiarias?nombre=vida`
- **THEN** el sistema devuelve solo entidades cuya `razonSocial` contiene `vida` (case-insensitive, contains) con 200

#### Scenario: Filtro por ubicación

- **WHEN** un cliente pide `GET /entidades-beneficiarias?ubicacion=siempreviva`
- **THEN** el sistema devuelve solo entidades cuya `direccion.calle` o `direccion.ciudad` contiene el texto (case-insensitive, contains) con 200

#### Scenario: Filtro por tipo con equivalencia

- **WHEN** un cliente pide `GET /entidades-beneficiarias?tipo=Comedores`
- **THEN** el sistema mapea `Comedores->COMEDOR` vía tabla de equivalencias y devuelve solo esas con 200; `tipo=Todas` o ausente devuelve todas

#### Scenario: Combinación de filtros

- **WHEN** un cliente pide `GET /entidades-beneficiarias?nombre=vida&ubicacion=caba&tipo=Salud`
- **THEN** el sistema devuelve solo entidades que cumplen las tres condiciones a la vez (AND) con 200

#### Scenario: Tipo desconocido

- **WHEN** un cliente pide `GET /entidades-beneficiarias?tipo=INEXISTENTE_XYZ`
- **THEN** el sistema responde 400 con mensaje y lista de valores válidos, sin devolver entidades

## Purpose

Permite al donante logueado saber cuántas entidades beneficiarias distintas ayudó en el mes en curso, para alimentar la tarjeta de impacto del browser de entidades con datos reales del backend.

## ADDED Requirements

### Requirement: Conteo mensual de entidades distintas por donante

The system SHALL expose `GET /donaciones-service/donaciones/donante/{idDonante}/entidades-ayudadas-este-mes` without date query params. The server SHALL compute the current month via its own clock (`YearMonth.now()`), count `COUNT(DISTINCT entidad_beneficiaria_id)` over donations where `idDonante = :id`, `fechaDeCreacion` falls within `[primer día del mes, último día del mes]`, and `necesidad_id IS NOT NULL`, and SHALL respond `{ donanteId, anio, mes, cantidad }` with HTTP 200.

#### Scenario: Donante con donaciones asignadas este mes

- **WHEN** un cliente pide el endpoint para un donante con 3 donaciones creadas este mes asignadas a 2 entidades distintas y 1 donación sin necesidad
- **THEN** el sistema responde 200 con `cantidad = 2` y `anio/mes` del mes actual del servidor

#### Scenario: Donante sin ayudas este mes

- **WHEN** un cliente pide el endpoint para un donante sin donaciones asignadas creadas este mes (vacío, solo del mes anterior, o solo sin necesidad)
- **THEN** el sistema responde 200 con `cantidad = 0` y el mes actual

#### Scenario: Donante inexistente

- **WHEN** un cliente pide el endpoint con un `idDonante` que no existe
- **THEN** el sistema responde 404 sin exponer conteo

#### Scenario: Donación de otro mes no cuenta

- **WHEN** una donación asignada fue creada el mes anterior aunque su último estado sea de este mes
- **THEN** el sistema la excluye del conteo (la fecha que manda es `fechaDeCreacion`)

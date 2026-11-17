## Purpose

Deja el browser de entidades del donante alimentado por datos reales del backend: tarjeta de impacto con el conteo mensual y listado filtrado por lo que el usuario escribe, manteniendo la página usable si el backend falla.

## ADDED Requirements

### Requirement: Tarjeta de conteo con datos reales para logueados

The system SHALL render the impact card on `/personaDonante/entidadesBeneficiarias` with the monthly distinct-entity count fetched for the session donor (`donanteIdActual`) from `GET /donaciones-service/donaciones/donante/{id}/entidades-ayudadas-este-mes`. For guests (`esInvitado`) the system SHALL NOT render the card (no fabricated number is shown). If the count request fails, the system SHALL render the page without the card figure and without template errors.

#### Scenario: Donante logueado abre el browser

- **WHEN** un donante identificado navega a `/personaDonante/entidadesBeneficiarias`
- **THEN** la tarjeta muestra su cantidad real de entidades distintas ayudadas este mes

#### Scenario: Invitado abre el browser

- **WHEN** un visitante no identificado navega a `/personaDonante/entidadesBeneficiarias`
- **THEN** la tarjeta de conteo no se renderiza y el resto de la página funciona igual

#### Scenario: Backend de conteo no disponible

- **WHEN** el request de conteo falla o expira para un donante logueado
- **THEN** la página renderiza igual (lista + filtros) sin la cifra, con mensaje de reintento, sin errores de template

### Requirement: Filtros del browser enviados al backend

The system SHALL read `busqueda` and `tipo` from the browser form's GET request on `/personaDonante/entidadesBeneficiarias`, forward them as query params to `GET /donaciones-service/entidades-beneficiarias`, render the returned entities, and preserve the active filter values in the form. Blank values SHALL be forwarded as absent (no filtering). If the list request fails, the system SHALL render an empty list with a retry message, without template errors.

#### Scenario: Usuario filtra por texto y tipo

- **WHEN** un visitante pide `/personaDonante/entidadesBeneficiarias?busqueda=vida&tipo=Salud`
- **THEN** el backend recibe `busqueda=vida&tipo=Salud`, la página muestra solo esas entidades y el form conserva "vida" y "Salud"

#### Scenario: Backend de listado no disponible

- **WHEN** el request de entidades falla o expira
- **THEN** la página muestra lista vacía con mensaje de reintento, sin errores de template

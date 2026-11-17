## Purpose

Garantizar que el entorno local levante de forma estable y repetible con un boot limpio, con seeds idempotentes y sincronización de donantes entre donaciones e incentivos por mismo ID.

## ADDED Requirements

### Requirement: Boot limpio levanta ambos servicios

El sistema SHALL permitir que `docker compose down -v + up --build` deje `incentivos-service` y `donaciones-service` en estado `running` sin intervención manual.

#### Scenario: Boot limpio sin intervención

- **WHEN** se ejecuta `down -v` seguido de `up --build`
- **THEN** ambos servicios completan su arranque y responden en sus puertos (8080/8081)

### Requirement: Seed de incentivos idempotente

El seed de incentivos SHALL ser seguro ante re-arranques con volumen persistente: si ya existen misiones/donantes, no SHALL duplicarlos.

#### Scenario: Re-arranque con volumen persistente

- **WHEN** se reinicia `incentivos-service` con `mysql-data` existente y ya seedado
- **THEN** no se crean nuevas misiones/categorías y `obtenerPrimeraMision` sigue devolviendo una única fila

### Requirement: POST donante no devuelve 500 por falta de misión

`POST /donante` en incentivos SHALL NOT responder `500` cuando aún no existe misión inicial; SHALL crear el donante aunque quede sin misión/categoría asignada.

#### Scenario: Donante llega antes que las misiones

- **WHEN** se hace `POST /donante` con `id` y `contactos` válidos y no hay misiones seedadas
- **THEN** el servicio responde `200` y persiste el donante

### Requirement: Donaciones reintenta arranque ante falla transitoria

`donaciones-service` SHALL reintentar su arranque automáticamente ante una falla transitoria de `incentivos-service` durante el seed, sin requerir `up` manual adicional.

#### Scenario: Incentivos tarda en estar listo

- **WHEN** `donaciones-service` falla su seed porque incentivos aún no responde correctamente
- **THEN** el contenedor reintenta y completa el seed cuando incentivos ya está listo

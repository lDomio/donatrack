## Why

`donaciones-service` no levanta en un boot limpio (`down -v + up --build`): su seed llama `POST incentivos:8081/donante` y recibe `500`, lo que hace rollback del seed y tumba el `ApplicationContext`. Esto bloquea seguir desarrollando.

## What Changes

- Hacer tolerante la creación de donante en incentivos cuando aún no hay misión seedada (no más `NPE` -> `500`).
- Hacer idempotente el seed de incentivos para que re-arranques con `mysql-data` persistente no dupliquen misiones/categorías/donantes.
- Agregar política de reintento a nivel compose para `donaciones-service` ante falla transitoria de orden de arranque.
- Verificar con boot limpio que ambos servicios quedan `running` y el seed queda consistente.

## Capabilities

### New Capabilities

- `dev-startup`: arranque local estable y repetible con seeds idempotentes y sincronización donaciones -> incentivos por mismo ID.

### Modified Capabilities

- Ninguna (no se cambian requirements existentes bajo `openspec/specs/`, que hoy no existe).

## Impact

- `incentivos-service/.../modelsEntities/donante/Donante.java` (constructor, null-guard).
- `incentivos-service/.../config/DataInitializer.java` (guard idempotente).
- `docker-compose.yml` (`donaciones-service.restart`).
- Sin cambios breaking de API; contrato `CrearDonanteRequest` se deja como está (`nombreUsuario` nullable) a propósito en este mínimo.
- Deuda explícita fuera de alcance: colisión semántica IDs 1,2,3 entre seeds, éxito degradado sin misión si hay race, orden `healthy` real.

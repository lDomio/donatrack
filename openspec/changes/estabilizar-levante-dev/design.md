## Context

Ver `proposal.md` (Why). Estado actual: `Donante.java:92-99` hace `misionActual.getCategoria()` sin null-check; `incentivos DataInitializer.java:42-72` siempre inserta sin guard; `docker-compose.yml:20-35` usa `service_started` para incentivos y `donaciones-service` no tiene `restart`. Contratos: `donaciones CrearDonanteRequest(id, contactos)` vs `incentivos (id, nombreUsuario, contactos)` — `nombreUsuario` llega null y es tolerado. IDs: se mantiene sincronía por mismo ID explícito con `IDENTITY` + `findById` early-return.

## Goals / Non-Goals

**Goals:**

- Eliminar el `500` determinístico en `POST /donante` por `NPE` y por `NonUniqueResult`.
- Hacer el seed de incentivos repetible sin `down -v` obligatorio.
- Permitir que un race transitorio se autocure con reintento a nivel compose.

**Non-Goals:**

- No cambiar el contrato de IDs (se mantiene mismo ID entre servicios).
- No agregar `actuator` ni `healthcheck` con nuevas dependencias en este mínimo.
- No hacer el seed de donaciones resiliente con `try/catch` (se deja que falle y reintente el contenedor para no enmascarar inconsistencias).
- No resolver la colisión semántica IDs 1,2,3 (personas distintas por servicio) — queda como deuda.

## Decisions

### 1. Null-guard en `Donante` en vez de fail-fast

Qué: `this.categoria = misionActual != null ? misionActual.getCategoria() : null` y `medioContacto null -> []`.
Por qué: convierte un crash de arranque en un éxito degradado temporal; con `down -v` limpio el caso casi no se da, y evita el `500`.
Alternativa considerada: lanzar `409/424` si no hay misión y forzar orden estricto — se descarta en mínimo porque requiere `healthcheck`/orden real y alarga el cambio.

### 2. Guard idempotente en `incentivos DataInitializer` en vez de migración de limpieza

Qué: early-return si `misionRepository.count() > 0 || donanteRepository.count() > 0`.
Por qué: una línea, evita duplicar misiones que rompen `obtenerPrimeraMision` (query de fila única). Compatible con volumen descartable y persistente.
Alternativa: query con `LIMIT 1 / ORDER BY` — se deja como opcional defensivo, no como fix principal porque esconde duplicados.

### 3. `restart: on-failure:5` en `donaciones-service` en vez de `service_healthy`

Qué: solo reintento a nivel contenedor, sin `healthcheck` nuevo.
Por qué: no hay `actuator` ni `curl/wget` garantizado en `eclipse-temurin:21-jre`; un `healthcheck` robusto exigiría nueva dependencia o Dockerfile. El `restart` cubre el race residual y es 1 línea.
Alternativa: agregar `spring-boot-starter-actuator` + `healthcheck` + `depends_on: healthy` — mejor orden real, pero excede el mínimo acordado; queda como follow-up.

## Risks / Trade-offs

- [Éxito degradado] Donante creado sin misión si hay race -> queda inconsistente sin reintento (no hubo excepción). Mitigación: con `down -v` limpio es raro; documentado como deuda; follow-up con orden `healthy` o fail-fast.
- [Colisión IDs] `1,2,3` significan personas distintas por servicio pero comparten ID por `findById` early-return. Mitigación: aceptado para dev por decisión explícita; no mirar nombres en tests E2E por ahora.
- [Seed donaciones atómico] Si falla `POST /donacion` tras donantes OK, toda la TX hace rollback y el retry repite todo. Mitigación: aceptable por guards `isEmpty()`; logs `IncentivosAdapter` muestran el punto de falla.

## Migration Plan

- Validación con volumen descartable: `docker compose down -v && docker compose up --build`.
- Rollback: revertir los 3 toques (son independientes y pequeños).
- Sin migración de datos: si la DB ya está duplicada, exigir un `down -v` (autorizado).

## Open Questions

- Ninguna que bloquee este mínimo. Deuda para después: ¿mismo-ID permanente o `externalId` separado? ¿`actuator` para orden real?

## Addendum (hallazgos de implementación)

- **IDs asignados**: `Donante.id` y `Donacion.id` en incentivos pasaron a asignados (sin `@GeneratedValue`). Con `IDENTITY` + ID asignado, `save()` hace `merge()` (falla `StaleObject` si la fila no existe) y `persist()` directo falla (`detached`). Creación usa `save()`/rama existe-merge, actualizaciones usan `merge()` explícito.
- **Conflicto `rabbitMQConfig`**: el refactor del notificador genérico (`common-lib`) duplicó el nombre de bean con donaciones/notificación; se dio nombre explícito a cada `@Configuration`.
- **Routing key fantasma**: `NotificarUsuarioEventPublisher` pedía `routing-keys.notificaciones` (comentada en el yml); apunta a `notificacion.general`.
- **Seed sin hardcode**: donaciones captura IDs de donantes/entidades en vez de `1L/2L/3L` (los rollbacks queman `AUTO_INCREMENT`).
- **Pendiente de entorno**: `mongo:27017` del compose choca con el `mongod` del host (sin sudo para frenarlo); `logistica-service` queda fuera hasta liberarlo (falla con `UnknownHostException: mongo` porque el container mongo quedó sin red al no poder bindear el puerto). Los donantes huérfanos 9-38 en incentivos (de reintentos fallidos) se limpian con un `down -v + up` final.
- **Tests**: el `EntityManager` se inyecta por constructor (el field injection no lo ve `@InjectMocks`); `DonacionServiceImplTest` mockea el EM y verifica `merge()` en updates.

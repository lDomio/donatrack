## Why

Los endpoints de conteo mensual y filtrado de entidades ya existen en `donaciones-service` (change `entidades-conteo-filtro`), pero el browser (`/personaDonante/entidadesBeneficiarias`) sigue mostrando un 33 hardcodeado e ignorando los filtros del form. Conectar el front cierra el loop y deja la vista con datos reales.

## What Changes

- El form existente (un solo campo `busqueda` + select `tipo`) hace GET con esos valores; el controller los lee con `@RequestParam` y los reenvía al backend. El form NO se parte en dos.
- Backend: `GET /donaciones-service/entidades-beneficiarias` suma el query param opcional `busqueda`, con semántica OR sobre nombre/ubicación (`razonSocial` O `calle` O `ciudad`, case-insensitive contains); `tipo` sigue en AND. `nombre`/`ubicacion` se preservan como filtros finos.
- La tarjeta de impacto reemplaza el 33 por el conteo real (`GET .../donaciones/donante/{id}/entidades-ayudadas-este-mes` con el `donanteId` de la sesión) para donantes logueados; invitados ven la tarjeta oculta o en 0 (a definir en diseño).
- Sin cambios en los endpoints ya existentes: todo lo nuevo es aditivo y backward-compatible.

## Capabilities

### New Capabilities

- `busqueda-entidades`: query param `busqueda` con semántica OR (nombre o ubicación) en el listado de entidades, combinado en AND con `tipo`.
- `wiring-entidades-front`: browser de entidades alimentado por backend (tarjeta de conteo real + filtros pasados al backend, con fallback ante backend caído).

### Modified Capabilities

- Ninguna (el change anterior está completo; `busqueda` es comportamiento nuevo, no cambio de lo especificado).

## Impact

- `donaciones-service`: `EntidadBeneficiariaController` (nuevo `@RequestParam`), `EntidadBeneficiariaService/Impl` (predicado OR + precedencia con `nombre`/`ubicacion`), `GlobalExceptionHandler` sin cambios (reutiliza 400 existente).
- `front-end-app`: `PersonaDonanteController` (`@RequestParam`, `donanteId` de sesión, atributos `cantidadEntidadesAyudadas`/`errorConteo`), `EntidadBeneficiariaApiService` (query params), `DonacionApiService` (nuevo método de conteo + DTO espejo), template `entidadesBeneficiarias.html` (reemplazar 33, preservar filtros activos en el form).

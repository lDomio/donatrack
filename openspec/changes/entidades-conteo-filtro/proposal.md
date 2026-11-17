## Why

El browser de entidades (`/personaDonante/entidadesBeneficiarias`) muestra un "33" hardcodeado en la tarjeta de impacto y un form de búsqueda (`busqueda` + `tipo`) que el backend ignora. El donante no puede saber cuántas entidades distintas ayudó este mes ni filtrar entidades por nombre/ubicación/tipo con datos reales.

## What Changes

- Nuevo endpoint de conteo (opción A): `GET /donaciones-service/donaciones/donante/{idDonante}/entidades-ayudadas-este-mes` sin query params de fecha; usa el mes actual del servidor (`YearMonth.now()`).
- Semántica "ayudar" = donación con `fechaDeCreacion` dentro del mes en curso y `necesidad != null` (creación asignada). Donaciones en depósito sin matchear se excluyen.
- Respuesta del conteo como objeto con contexto: `{ donanteId, anio, mes, cantidad }`.
- Extensión backward-compatible de `GET /donaciones-service/entidades-beneficiarias` con query params opcionales `nombre`, `ubicacion`, `tipo` (combinación AND; ausente/vacío = no filtra).
- Filtro `tipo` mediante tabla de equivalencias front->backend (`Comedores->COMEDOR`, `Educación->EDUCACION`, etc.); `Todas`/vacío = sin filtro; valor desconocido = 400 con valores válidos.
- Sin cambios visuales en esta fase: el front integrará estos endpoints en un paso posterior (reemplazar el 33 y pasar los filtros).

## Capabilities

### New Capabilities

- `conteo-entidades-ayudadas`: conteo mensual de entidades beneficiarias distintas ayudadas por un donante (definición, endpoint, respuesta, errores).
- `filtro-entidades-beneficiarias`: filtrado opcional del listado de entidades por nombre (razón social), ubicación (calle + ciudad) y tipo (con equivalencias).

### Modified Capabilities

- Ninguna (no existe `openspec/specs/` base; el listado sin filtros de `entidad-beneficiaria` del change anterior se preserva como caso sin params).

## Impact

- `donaciones-service`: `DonacionController` (nuevo GET), `DonacionService` + `DonacionRepository` (query `COUNT DISTINCT entidad` por `idDonante` + rango de mes), nuevo DTO de respuesta, `GlobalExceptionHandler` (404 donante inexistente).
- `donaciones-service`: `EntidadBeneficiariaController` + `EntidadBeneficiariaService/Impl` + `EntidadBeneficiariaRepository` (filtros opcionales; posible JPQL/Specification o filtrado con normalización case-insensitive).
- `front-end-app` (fuera de alcance de implementación de este change, solo contrato): `PersonaDonanteController`, `EntidadBeneficiariaApiService`, `DonacionApiService`, template `entidadesBeneficiarias.html` (reemplazar 33, cablear `busqueda`->`nombre`+`ubicacion`).

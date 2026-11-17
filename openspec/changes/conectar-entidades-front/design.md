## Context

Ver `proposal.md`. Estado actual: `donaciones-service` expone el conteo (`GET /donaciones/donante/{id}/entidades-ayudadas-este-mes`, respuesta `{donanteId, anio, mes, cantidad}`) y el listado con `nombre/ubicacion/tipo` en AND (change `entidades-conteo-filtro`, completo). El front (`PersonaDonanteController.entidadesBeneficiarias`, `EntidadBeneficiariaApiService`, template `entidadesBeneficiarias.html`) no consume ninguno: tarjeta con 33 fijo, form sin `@RequestParam`.

## Goals / Non-Goals

**Goals:**
- Un solo texto `busqueda` con OR (decisión del usuario: no partir el form).
- Tarjeta real para logueados, oculta para invitados (default elegido: mostrar 0 a un invitado sería falso, no sabemos si ayudó).
- Fallback graceful ante backend caído, como ya hace la vista (patrón `errorCarga`).

**Non-Goals:**
- Paginación, autocompletado, resaltado de matches.
- Cambiar la tabla de equivalencias de `tipo` (se reutiliza tal cual).
- Guest con conteo (no hay identidad; queda fuera).

## Decisions

- **`busqueda` como OR en backend, no dos llamadas desde el front:** mandar el mismo texto a `nombre`+`ubicacion` daría casi vacío por el AND actual (ej: "bajitos" matchea nombre pero no ubicación). Alternativa "front une dos llamadas" descartada: duplica requests y exige deduplicar/ordenar en el front. `busqueda` es un `@RequestParam` opcional más, backward-compatible; `nombre`/`ubicacion` se preservan para API/Postman.
- **Precedencia si vienen ambos:** si `busqueda` está presente y no blank, se aplica el OR y se ignoran `nombre`/`ubicacion` (evita AND contradictorios); si no, rige la lógica actual. Alternativa "AND de todo" descartada por el motivo anterior.
- **Front reenvía en crudo:** `PersonaDonanteController` recibe `busqueda`/`tipo` y los pasa al ApiService sin normalizar (la normalización vive en el backend, un solo lugar). Blank → no se envía el param.
- **Conteo solo logueado vía `donanteIdActual`:** reutiliza `IdentidadService` como hace `misDonaciones`; invitado → sin tarjeta (`th:if`). Fallo del conteo → página sin cifra (flag `errorConteo`), independiente del flag `errorCarga` del listado.
- **DTO espejo en front:** `EntidadesAyudadasResponse` con `@JsonIgnoreProperties(ignoreUnknown = true)`, igual que el espejo de entidades existente.

## Risks / Trade-offs

- [OR más permisivo] `busqueda` devuelve más resultados que los filtros finos → Mitigación: es lo que el placeholder "nombre o ubicación" promete; orden sin cambios (el del backend).
- [Doble request en el browser] Lista + conteo son 2 llamadas (conteo solo logueado) → Mitigación: independientes, pueden ir en paralelo; timeout 5s como el existente.
- [Invitado sin tarjeta] Cambia levemente el layout actual para guests → Mitigación: la tarjeta con cifra inventada (33) sale de todos modos; aceptado como default, fácil de revertir a "mostrar 0".

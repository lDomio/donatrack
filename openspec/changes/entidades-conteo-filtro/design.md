## Context

Ver `proposal.md` para motivación. Estado actual: `DonacionController` expone `GET /donante/{id}` (lista) pero ningún agregado; `EntidadBeneficiariaController.GET /` no acepta params y `EntidadBeneficiariaImpl.obtenerTodas()` hace `findAll()` + mapeo en memoria. Modelo: `Donacion --(necesidad_id nullable)--> Necesidad --(entidad_beneficiaria_id)--> EntidadBeneficiaria{razonSocial, tipo{tipoEntidad string libre}, direccion{calle, altura, ciudad{Nombre}}}`. Front (`entidadesBeneficiarias.html`, `PersonaDonanteController`, `EntidadBeneficiariaApiService`) ya tiene UI de filtros y tarjeta con 33 hardcodeado, fuera de alcance de este change.

## Goals / Non-Goals

**Goals:**
- Conteo con semántica "creación asignada" y respuesta con contexto (`donanteId, anio, mes, cantidad`).
- Filtro backward-compatible sobre el GET existente con `nombre/ubicacion/tipo` opcionales + tabla de equivalencias de tipo.
- Queries eficientes a nivel repositorio donde sea posible, sin romper `obtenerTodas()` sin params.

**Non-Goals:**
- Integración front (nuevo change): cablear tarjeta y form, mapear `busqueda -> nombre+ubicacion`.
- Paginación/ordenamiento, búsqueda full-text con acentos, geo-radio por lat/long.
- Cambiar `TipoEntidad` a enum o migraciones de datos.

## Decisions

- **Conteo en `DonacionController` (opción A):** `GET /donaciones/donante/{idDonante}/entidades-ayudadas-este-mes`. Alternativa B (en `EntidadBeneficiariaController`) descartada: el agregado nace de donaciones por donante y reutiliza la validación `donanteService.obtenerDonante(id)` → 404. Sin `anio/mes` en request por decisión de UX; la respuesta incluye `anio/mes` del servidor para que el cliente no adivine.
- **Fecha = `Donacion.fechaDeCreacion` (LocalDate) + rango `[YearMonth.now().atDay(1), atEndOfMonth()]`.** Alternativa "fecha de último estado / entrega" descartada por el usuario (más fiel pero exige recorrer `estadosDeDonaciones` y definir qué estados cuentan). `fechaDeCreacion` es columna directa, indexable, y `necesidad IS NOT NULL` excluye depósito sin matchear.
- **Query conteo:** JPQL `SELECT COUNT(DISTINCT n.entidadBeneficiaria.id) FROM Donacion d JOIN d.necesidad n WHERE d.idDonante = :id AND d.fechaDeCreacion BETWEEN :ini AND :fin` en `DonacionRepository`. Alternativa in-memory (`findByIdDonante` + stream + distinct) descartada por N+1 y LAZY de `necesidad/entidad`. Nuevo DTO `EntidadesAyudadasResponse(donanteId, anio, mes, cantidad)`.
- **Filtro extendiendo GET existente** con `@RequestParam(required=false)`. Alternativa endpoint nuevo (`/buscar`, `/filtradas`) descartada: duplicaría contrato y rompería la llamada actual del front. `Service.obtenerTodas()` se sobrecarga o recibe un filtro; `Repository` usa JPQL con `LOWER(...) LIKE %...%` sobre `razonSocial`, `direccion.calle`, `direccion.ciudad.nombre` + predicado de tipo. Si `direccion.ciudad` es null-safe, el predicado debe tolerarlo.
- **Tipo vía tabla de equivalencias en service (no en DB):** normaliza trim + upper + sin acentos + singular/plural (`COMEDORES->COMEDOR`, `EDUCACION/EDUCACIÓN->EDUCACION`, `SALUD`, `REFUGIO/REFUGIOS`, `ONG`, `TODAS->sin filtro`). Valor desconocido → 400 con lista válida vía `BusinessException` + `GlobalExceptionHandler`. Alternativa igualdad exacta descartada (front manda "Comedores" vs "COMEDOR" y daría vacío).
- **Reloj inyectable (`Clock` bean, zona `America/Argentina/Buenos_Aires`):** el servicio recibe `Clock` para `YearMonth.now(clock)`; tests fijan el clock en borde de mes. Alternativa `LocalDate.now()` directo descartada por no testeable.

## Risks / Trade-offs

- [Server clock vs user timezone] Un donante en otra zona podría ver "este mes" distinto al suyo → Mitigación: documentar que el mes es del servidor; respuesta incluye `anio/mes` para mostrarlo explícito.
- [TipoEntidad string libre] Datos históricos con variantes (`Comedor`, `comedor`, `ONG`) pueden no matchear → Mitigación: normalización + log de tipos no mapeados; no migrar datos en este change.
- [Ubicación solo calle+ciudad] `DireccionDto` no expone país y `Ciudad.pais` existe en entidad → Mitigación: v1 solo calle+ciudad; exponer país queda como follow-up si se necesita.
- [LIKE %...% sin índice full-text] Con miles de entidades el filtro es secuencial → Mitigación: aceptable en v1; índice funcional `LOWER(razonSocial)` como follow-up si hay lentitud.
- [Donación asignada pero luego desasignada (ENTREGA_FALLIDA → desasignar)] El conteo por `necesidad IS NOT NULL` la excluye correctamente, pero una donación creada este mes y desasignada este mes deja de contar aunque "intentó ayudar" → Aceptado por semántica elegida; documentar.

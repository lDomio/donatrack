## Context

Ver `proposal.md` para motivacion. Estado actual: `entidadesBeneficiarias.html` modal solo lectura sin form; `verNecesidadEntidad.html` form maqueta sin action y con `type="button"`; `NecesidadDto(id, subcategoria:string, ...)` sin id/unidad; backend `BienRequest` prefiere unidad del catalogo cuando hay `subcategoriaId`.

## Goals / Non-Goals

**Goals:**
- Un unico fragmento modal multi-insumo con validacion por fila.
- Contrato `NecesidadDto.subcategoria:{id,nombre,unidadDeMedida}` reutilizando shape `SubcategoriaSimpleResponse`.
- POST unificado frontend -> donaciones-service con `bienes[]`.

**Non-Goals:**
- Logistica/entrega, matching, incentivos.
- Subida de fotos y vencimiento (se dejan null en este corte).
- Nuevo design system CSS.

## Decisions

- **Objeto anidado vs campos planos:** objeto `subcategoria:{id,nombre,unidadDeMedida}` sobre campos planos, porque reutiliza `SubcategoriaSimpleResponse` existente y evita triple prefijo `subcategoriaId/Nombre/Unidad`. Alternativa plana descartada por ruido en firma del record.
- **Breaking inmediato vs aditivo:** reemplazo directo del `string`, sin convivencia, por decision explicita del usuario. Alternativa aditiva descartada para no mantener doble fuente.
- **Opcion B (checkbox + stepper) vs `<select multiple>` nativo:** B, porque muestra faltante/barra/max por fila y funciona en mobile. Nativo descartado por ocultar faltante.
- **Unidad por fila vs global:** por fila con lista hardcodeada `unid, kg, g, L, mL, cajas, bultos, m, m2`, preseleccionada con unidad del catalogo. Global descartado porque Aceite (L) y Arroz (kg) conviven.
- **Fragmento `fragments/modalDonacion.html` con param `idBase`:** evita IDs duplicados (`modalDonar-{entidadId}` vs `modalDonar-{entidadId}-{catIdx}`) y unifica CSS `.formularioDonacion` y validacion JS.
- **Cantidad por insumo `1 <= cant_i <= faltante_i`:** validacion cliente (stepper + clamp) y servidor (400 si excede). Total global descartado.

## Risks / Trade-offs

- [Riesgo] `subcategoria` null en Necesidad -> `subcategoriaDto` null, modal sin unidad ni hidden id -> Mitigacion: fila en modo fallback solo nombre+cantidad, POST sin `subcategoriaId` usa unidad hardcodeada (rama `else if` existente).
- [Riesgo] Divergencia unidad elegida vs catalogo (backend pisa silencioso cuando hay id) -> Mitigacion: hint "Unidad sugerida: L (catalogo)" en la fila.
- [Riesgo] N modales x N forms en la misma pagina con mismos `name` -> Mitigacion: `idBase` unico + `name="bienes[{idx}].cantidad"` indexado por fila.
- [Riesgo] Cambio breaking rompe consumidores viejos -> Mitigacion: actualizar tests + Postman en el mismo change, sin compat.

## Migration Plan

1. Merge backend (DTO + 2 mappers) con tests en verde.
2. Merge frontend (espejo + mappers + controller POST + fragmento + Postman docs).
3. Rollback: revert del change completo (no hay migracion de datos, solo contrato JSON).

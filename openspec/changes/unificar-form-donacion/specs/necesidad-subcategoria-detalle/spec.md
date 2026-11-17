## Purpose

Exponer subcategoria con id, nombre y unidad de medida en cada necesidad para que el frontend pueda donar al destino correcto con la unidad del catalogo.

## ADDED Requirements

### Requirement: Necesidad expone subcategoria detallada
El sistema SHALL devolver en cada necesidad un objeto subcategoria con id, nombre y unidadDeMedida cuando la necesidad tiene subcategoria asociada, y null cuando no la tiene.

#### Scenario: Necesidad con subcategoria
- **WHEN** se lista una entidad con necesidad de Aceite (id 7, unidad L)
- **THEN** el JSON incluye `subcategoria: {id: 7, nombre: "Aceite", unidadDeMedida: "L"}`

#### Scenario: Necesidad sin subcategoria
- **WHEN** la necesidad no tiene subcategoria asociada
- **THEN** el JSON incluye `subcategoria: null`

## REMOVED Requirements

### Requirement: Subcategoria como string plano
**Reason**: Reemplazado por objeto detallado para donar con id y unidad.
**Migration**: Leer `subcategoria.nombre` en lugar de `subcategoria`, `subcategoria.id` como `subcategoriaId`, `subcategoria.unidadDeMedida` como unidad sugerida.

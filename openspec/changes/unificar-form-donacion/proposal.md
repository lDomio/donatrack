## Why

El boton "Donar ahora" hoy abre un modal solo informativo sin formulario, y existe otro formulario maqueta en verNecesidad sin action ni POST. Unificar ambos en un unico modal funcional permite donar multi-insumo con cantidad limitada por faltante, unidad y subcategoriaId reales.

## What Changes

- **BREAKING**: `NecesidadDto` reemplaza `subcategoria: string` por objeto `subcategoria: {id, nombre, unidadDeMedida}` (reutilizando shape de `SubcategoriaSimpleResponse`).
- Agrega fragmento Thymeleaf compartido `fragments/modalDonacion` usado por `entidadesBeneficiarias` y `verNecesidadEntidad`.
- Modal unificado opcion B: lista tildable multi-insumo + stepper cantidad por fila con `max = faltante`.
- Select de unidad de medida hardcodeado por fila, preseleccionado con unidad del catalogo cuando existe.
- `POST /personaDonante/donaciones` en frontend + `DonacionApiService.crear()` hacia `POST donaciones-service/donaciones` con `bienes[] {nombre, cantidad, subcategoriaId, unidadDeMedida}`.
- Actualiza tests backend + frontend y colecciones Postman al nuevo contrato.

## Capabilities

### New Capabilities

- `donacion-modal-unificado`: modal compartido multi-insumo con validacion por fila y POST unificado.
- `necesidad-subcategoria-detalle`: Necesidad expone subcategoria con id/nombre/unidad para donar.

### Modified Capabilities

- Ninguna (no hay specs previas en `openspec/specs/`).

## Impact

- Backend: `NecesidadDto`, `EntidadBeneficiariaImpl.mapearNecesidad`, `DonacionServiceImpl.mapeoNecesidades`.
- Frontend: `NecesidadResponse`, `NecesidadResumenDto`, `InsumoNecesidadDto`, `EntidadVistaMapper`, `EntidadDetalleMapper`, `PersonaDonanteController`, `DonacionApiService`, templates `entidadesBeneficiarias.html`, `verNecesidadEntidad.html`, nuevo `fragments/modalDonacion.html`, CSS/JS compartidos.
- Tests backend y frontend que construyen `new NecesidadDto(...)` o `NecesidadResponse`.
- Postman: `donatrack-service.postman_collection.json`, `DonaTrack_E2E_Completo.postman_collection.json`.

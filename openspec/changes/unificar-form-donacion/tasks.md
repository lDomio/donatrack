## 1. Contrato backend NecesidadDto

- [x] 1.1 Reemplazar `subcategoria: String` por objeto `{id, nombre, unidadDeMedida}` en `NecesidadDto` y verificar compilacion de `donaciones-service`.
- [x] 1.2 Actualizar `EntidadBeneficiariaImpl.mapearNecesidad` y `DonacionServiceImpl.mapeoNecesidades` a nuevo objeto con null-safe y verificar tests de mapeo en verde.
- [x] 1.3 Actualizar tests backend (`EntidadFiltroServiceTest`, `EntidadRepositoryFiltroTest`, `EntidadFiltroControllerTest`) al nuevo shape y verificar `mvn -pl donaciones-service test` en verde.

## 2. Espejo frontend + validacion

- [x] 2.1 Actualizar `NecesidadResponse` al objeto subcategoria y extender `NecesidadResumenDto` e `InsumoNecesidadDto` con `subcategoriaId + unidadSugerida + faltante` y verificar deserializacion de ejemplo.
- [x] 2.2 Actualizar `EntidadVistaMapper` y `EntidadDetalleMapper` a nuevo shape con fallback null y verificar `EntidadVistaMapperTest` en verde.
- [x] 2.3 Agregar `POST /personaDonante/donaciones` en `PersonaDonanteController` + `DonacionApiService.crear()` con lista de unidades hardcodeadas y verificar POST a `donaciones-service/donaciones` con mock.

## 3. Modal unificado B

- [x] 3.1 Crear `fragments/modalDonacion.html` con param `idBase`, checkbox multi + stepper por fila (`max=faltante`), unidad por fila y mensaje/contacto globales, y verificar render sin IDs duplicados.
- [x] 3.2 Reemplazar modales de `entidadesBeneficiarias.html` y `verNecesidadEntidad.html` por el fragmento y verificar apertura/cierre `:target` intacta.
- [x] 3.3 Unificar CSS `.formularioDonacion` + JS de habilitacion/validacion por fila y boton `Confirmar (N)` y verificar comportamiento con error `cant > faltante` bloqueando submit.

## 4. Docs y cierre

- [x] 4.1 Actualizar Postman (`donatrack-service`, `DonaTrack_E2E_Completo`) al nuevo `NecesidadDto` y verificar ejemplo con objeto subcategoria.
- [x] 4.2 Verificacion E2E: tildar 2 insumos con cantidades validas y unidad, confirmar y verificar donacion creada y metricas actualizadas.

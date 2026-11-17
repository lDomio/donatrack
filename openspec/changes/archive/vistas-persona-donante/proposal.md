## Why

Las vistas de persona donante son stubs de 7 líneas de Thymeleaf, pero su CSS ya está escrito por completo y nunca se renderiza. Como el front-end no tiene JavaScript y tampoco datos conectados, el área de persona donante no es demostrable ni revisable. Conectar el backend ahora bloquearía el avance por DTOs incompletos (dirección ausente en `EntidadBeneficiariaResponse`, sin endpoint de tiempo estimado de ruta), así que primero se renderizan las vistas con datos de ejemplo y se fija el contrato de DTOs que se usará al integrar.

## What Changes

- Renderizar `mapaDonaciones`, `entidadesBeneficiarias` y `verNecesidadEntidad` a partir de sus CSS existentes, sin modificar los CSS.
- Incorporar `misDonaciones` al mismo esquema de datos de ejemplo, para que las cuatro vistas de persona donante muestren contenido consistente (hoy renderiza listas vacías).
- Introducir records de DTO de vista (`DonacionVistaDto`, `EntidadVistaDto`, `PuntoCercanoDto`, `PinMapaDto`, `CategoriaNecesidadDto`, `InsumoNecesidadDto`) siguiendo la convención de `dto/` ya usada por `DonanteDto` y `MedioContactoDto`.
- Servir los datos de ejemplo desde `PersonaDonanteController` con `List.of(...)`, sin capa de persistencia ni servicio nuevo.
- Implementar dos modales sin JavaScript, mediante `:target` sobre ids generados por `th:each`.
- Cambiar la ruta de detalle de necesidades a `/personaDonante/verNecesidadEntidad/{id}`.

### Fuera de alcance (decidido explícitamente)

- Modificar CSS, `back_services`, o DTOs del `donaciones-service`.
- Conectar datos reales. El mapa queda como imagen decorativa con pines de posición fija, y los tiempos estimados de ruta son inventados porque el backend no los expone.

## Capabilities

### New Capabilities

- `mis-donaciones`: vista deMis donaciones de la persona donante, alimentada por `DonacionVistaDto`.
- `mapa-donaciones`: mapa decorativo de donaciones en ruta, con filtros, pines y puntos cercanos.
- `entidades-beneficiarias`: grilla de entidades beneficiarias con filtros, métricas, impacto y modal de necesidades.
- `detalle-necesidades-entidad`: detalle de una entidad y sus categorías de necesidades, con modal de donación.

### Modified Capabilities

Ninguna. No hay specs existentes en `openspec/specs/`.

## Impact

- **Controller**: `front-end-app/.../controllers/PersonaDonanteController.java` — 4 handlers pasan de listas vacías a datos de ejemplo; `verNecesidadEntidad` pasa a recibir `id`.
- **DTOs nuevos**: 6 records en `front-end-app/.../dto/`.
- **Templates**: se completan `mapaDonaciones.html`, `entidadesBeneficiarias.html`, `verNecesidadEntidad.html`; se ajusta `misDonaciones.html` al `DonacionVistaDto` compartido.
- **Sin cambios**: CSS, `back_services`, `donaciones-service`, layout base, fragments de header y footer.
- **Deuda que queda registrada**: `.piePaginaPrincipal` y `.contenedorPiePagina` están definidos en 4 CSS de vista pero ningún template los renderiza; el footer real usa `.piePagina` desde `fragments/footer.html`. No se toca.
- **Deuda que queda registrada**: las barras de progreso no tienen `width` en el CSS, por lo que el ancho se aplica con `th:style` inline. Los pines del mapa sí tienen posición fija en el CSS, por lo que no escalan a más de 3.

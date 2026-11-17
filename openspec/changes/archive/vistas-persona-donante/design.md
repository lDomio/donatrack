## Context

Ver `proposal.md` para la motivación. Restricciones observadas que condicionan el diseño:

- El front-end **no tiene JavaScript**. No hay un solo `.js` en `static/`. El menú lateral móvil ya resuelve interacciones con `:target` y `:focus-within`.
- Cada vista declara su CSS con un fragmento `estilosExtra` de un solo `<link>`; `headHtml.html` lo inyecta con `th:replace="~{:: estilosExtra ?: ~{}}"`.
- `headHtml.html` carga 3 CSS compartidos (`variablesHeaderFooter.css`, `compartidoEntidadBeneficiaria.css`, `compartidoPersonaDonante.css`). No existe un `estilos.css`.
- El header y el footer se inyectan desde `fragments/header.html` y `fragments/footer.html` vía `layout/base.html`. Las vistas solo aportan el fragmento `contenido`.
- Los DTOs del front-end son `record`s en `dto/`. **No existe patrón interface**: los services son clases concretas `@Service`, y los `back_services` son stubs con `WebClient` sin usar.
- Los CSS de las 4 vistas de persona donante están enteramente escritos a mano y son mucho más ricos que los bocetos de Excalidraw. En `verNecesidadEntidad` el boceto no aporta estructura: el CSS es la única especificación.

## Goals / Non-Goals

**Goals:**

- Que las 4 vistas rendericen contenido real contra los CSS existentes, sin tocar un solo CSS.
- Fijar los DTOs de vista como el contrato que se reutilizará al conectar el backend.
- Que la sustitución de mocks por datos reales sea un cambio de una línea por handler.

**Non-Goals:**

- No se persisten donaciones. El confirm del formulario de donación no envía nada.
- No se implementan los filtros. Se renderizan y se pueblan, pero no filtran (requeriría JS o GET con query params; ver Open Questions).
- No se construye el mapa interactivo.

## Decisions

### D1. Los mocks viven en el controller con `List.of(...)`

Se descarta una clase `DatosDeEjemplo` (`@Component`) y un JSON de recursos.

**Por qué:** el proyecto no tiene patrón interface ni capa de repositorio; los controllers ya pasan `List.of()` directo. Un `@Component` agregaría indirección que después queda huérfana. Con los mocks en el controller, la línea que hay que cambiar al conectar es exactamente una por handler:

```java
// ahora
model.addAttribute("entidades", List.of(new EntidadVistaDto(...), ...));
// después
model.addAttribute("entidades", entidadApiService.obtenerTodas());
```

**Costo aceptado:** `PersonaDonanteController` crece. Es tolerable porque los datos desaparecen al conectar.

### D2. Los DTOs de vista son `record`s en `dto/`, alineados con los nombres del backend

Se crean seis records:

```java
public record DonacionVistaDto(Long id, String titulo, String categoria, String subcategoria,
                               String descripcion, String estado, String imagenUrl, boolean destacado)
public record EntidadVistaDto(Long id, String razonSocial, String tipo, String claseInsignia,
                              String ubicacion, String descripcion, String imagenUrl,
                              List<MetricaVistaDto> metricas, int progresoPorcentaje,
                              String claseColorProgreso, int impacto, String iconoImpacto,
                              List<NecesidadResumenDto> necesidades)
public record MetricaVistaDto(String nombre, int cantidadRequerida, int cantidadDonada,
                              int porcentaje, boolean critica)
public record NecesidadResumenDto(String nombre, int cantidadRequerida, int cantidadDonada,
                                  int porcentaje, boolean urgente, String claseColorProgreso)
public record PuntoCercanoDto(Long id, String nombre, String direccion, String horario,
                              boolean destacado, String etiquetaEstado, String tiempoEstimado)
public record PinMapaDto(Long id, String nombre, String clasePosicion, String etiquetaEstado,
                         String tiempoEstimado, String enlace, boolean esCamionEnMovimiento)
```

Alinear los nombres con `EntidadBeneficiariaResponse` y `NecesidadDto` (no inventar nombres nuevos) para que el diff posterior sea mecánico. `tipo` conserva el string crudo del backend y la clase de insignia se resuelve en la vista, no en el record.

### D3. El ancho de las barras de progreso va inline; el color va por clase

`.barraProgreso` tiene `width: 100%` pero `.progresoRelleno` y `.barraRelleno` **no tienen `width`**, y existen 4 clases de color (`.teal .orange .red .green`) y 2 modificadores de estado (`.alerta`, `.completado`).

```html
<div class="contenedorBarra">
  <div class="barraRelleno teal" th:style="'width:' + ${insumo.porcentaje} + '%'"></div>
</div>
```

**Alternativa descartada:** generar una clase por porcentaje. El CSS no está escrito para eso y las barras tienen `transition: width 0.5s ease`, que asume que el ancho cambia en runtime. Separar continuo (inline) de discreto (clase) es lo que el CSS ya implica.

### D4. La posición de los pines viene del dato, pero el CSS la fija

`.pinTrasladoActivo`, `.pinDestinoEscuela` y `.pinEntregadoComedor` tienen `top`/`left` hardcodeados y no hay más clases. `PinMapaDto`Transporta `clasePosicion` y el `th:each` lo aplica con `th:classappend`, en vez de hardcodear 3 `<div>` en el HTML.

**Por qué:** mantiene al controller como única fuente de datos, coherente con D1, y hace que la estructura del template sea la definitiva. Se acepta que solo hay 3 posiciones disponibles (ver R1).

**Alternativa descartada:** `style="top:..%;left:..%"` inline generaliza a N pines, pero deja las 3 clases del CSS muertas y contradice la decisión de "respetar los class names del CSS".

### D5. Los modales usan `:target` con ids generados por `th:each`

```html
<a class="botonDonarAhora" th:href="'#modalDonar' + ${e.id}">Donar ahora</a>
...
<div class="modalContenedor" th:id="'modalDonar' + ${e.id}"> ... </div>
```

`.modalContenedor:target` ya activa la visibilidad y `.botonCerrarModal` es un `<a href="#">` que saca el target. Es el mismo mecanismo que ya usa el menú lateral móvil.

**Alternativas descartadas:** `display:none` + checkboxRequires JS. `onclick` requiere JS. Ninguna es viable.

### D6. `verNecesidadEntidad` pasa a `/{id}`

El layout base **no** linkea esta vista: se llega solo desde `.botonVerNecesidades` de una card. Cambiar a `/verNecesidadEntidad/{id}` no rompe ningún enlace existente y es la única forma de que una entidad real lleve a *sus* necesidades. El mapeo sin path param se elimina, para que la spec "no se sirve sin identificador" sea cierta.

### D7. No se escribe footer en las vistas

`.piePaginaPrincipal` y `.contenedorPiePagina` están definidos en `mapaDonaciones.css`, `entidadesBeneficiarias.css`, `verNecesidadEntidad.css` e `incentivos.css`, pero **ningún template del proyecto los renderiza**. El footer real usa `.piePagina` y `.contenedorPie` desde `fragments/footer.html`, que `base.html` ya inyecta. Esas clases son código muerto; ignorarlas evita duplicar el footer bajo el real.

## Risks / Trade-offs

- **[R1] Los pines no escalan a más de 3.** Cualquier cuarto punto se apila en la posición de su rol. → Se acepta como límite del mock. Al conectar el backend, hay que elegir entre position inline o generar clases nuevas; la decisión se difiere hasta entonces.
- **[R2] Los mocks pueden divergir del contrato real.** Es el riesgo principal: si los nombres de campo no coinciden con `EntidadBeneficiariaResponse`/`NecesidadDto`, el swap deja de ser mecánico. → Mitigación D2 (alinear nombres) más verificar campo por campo contra los DTOs del `donaciones-service` antes de conectar.
- **[R3] `EntidadBeneficiariaResponse` no expone `direccion`.** La entidad sí tiene `Direccion` con `latitud`, `longitud`, `ciudad`, `calle` y `altura`, pero el DTO de respuesta no la incluye. Esto bloquea a la vez el mapa y el `.ubicacionEntidad` de las cards. → El mock inventa la ubicación. Al conectar hay que agregar `direccion` a `EntidadBeneficiariaResponse`, que es un cambio en el `donaciones-service` y queda fuera de este change.
- **[R4] `:target` muta la URL.** Abrir un modal cambia el fragmento a `#modalDonar3`; el botón atrás lo cierra. Rompe el deep-linking a modales y puede hacer scroll al usar el control de cerrar. → Aceptado como precio de no tener JS. Con JS, un `dialog` element o una clase toggle lo resuelven.
- **[R5] El enum de estado tiene 7 valores y el CSS solo diferencia 2.** `.estadoValor.activo` (en tránsito) y `.exito` (entregada) existen; `DEPOSITO`, `VENCIDO`, `ENTREGA_FALLIDA` y `SIN_CONFIRMAR_RECEPTOR` no tienen tratamiento. → La spec exige que esos estados se rendericen legibles sin énfasis, no que se distinguen entre sí.
- **[R6] Los filtros no filtran.** Se renderizan y se pueblan, pero no aplican ningún criterio porque no hay JS. → Declarado como Non-Goal. La spec solo exige que se rendericen.
- **[R7] La barra de progreso se calcula en el mock.** `porcentaje` viene del record, no del template. Al conectar hay que calcularlo desde `cantidadDonada`/`cantidadRequerida`. → Se calcula en el DTO, no en el HTML, para que el cambio sea local.

### Migration Plan

Sin migración de datos ni despliegue. Rollback = revertir los 4 templates y el controller. No se toca `donaciones-service`, así que no hay contrato de API que revertir.

## Open Questions

- **¿Los filtros deben filtrar de verdad?** Se resolvería con parámetros de query en el mapeo del controller (Thymeleaf ya tiene `@ModelAttribute`), sin JS. Si se quiere, es un change aparte: no altera el DTO ni el resto de la estructura.
- **¿Qué pasa con los 4 pines fijos al conectar?** Depende de si se elige una librería de mapas o se mantiene la imagen decorativa. Se difiere porque no cambia las specs de este change.

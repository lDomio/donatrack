## Context

Ver `proposal.md` para la motivacion, `plan.md` para el reparto en hitos (Hito 0, V1-V5) y `specs/` para el comportamiento. Estado actual que condiciona el diseno:

- `PersonaDonanteController` servia 4 vistas con `List.of(...)` mocks (change `vistas-persona-donante` completo). Hito 0, V3 (mis donaciones) y V4 (incentivos) ya estan implementados con las reglas de este diseno; pendientes V1, V2 (vidriera) y V5 (mapa).
- Backend `donaciones-service`: `GET /donaciones-service/entidades-beneficiarias`, `GET .../{id}`, `GET /donaciones-service/donaciones/donante/{idDonante}`. `EntidadBeneficiariaResponse(id, razonSocial, tipo, contactos, representantes, necesidades)` no expone `direccion`/`descripcion`/`imagenUrl` aunque la entidad JPA si tiene `Direccion`. `MedioDeContactoDTO` es `(tipo, valor)`: no sirve como fallback de ubicacion.
- Backend `incentivos-service` (`:8081`, sin prefijo): `GET /incentivos/{id}/misiones` (solo completadas: `MisionResponse(insignia, fecha)`), `GET /incentivos/{id}/insignias` (`InsigniaResponse(descripcion)`), `GET /incentivos/{id}/metricas` (sin datos utiles para la vista), `GET /incentivos/{id}/estadoGeneral` (stub vacio). `{id}` es el mismo `idDonante` de `donaciones-service`.
- Backend `logistica-service`: `GET /logistica-service/rutas`, `/rutas/{id}`, `/entregas/pendientes`, `/entregas/{id}`. Sin consulta por donante.
- Auth inexistente: `CustomAuthProvider.authenticate()` devuelve `null`, `SecurityConfig` esta en `anyRequest().permitAll()` (el `authenticationEntryPoint` nunca se dispara), `login.html`/`registro.html` vacios, `autenticacion-service` sin codigo. `WebApiCallerService.executeWithTokenRetry` lanza `RuntimeException` sin `session.accessToken` (nadie lo escribe) y los servicios del back no validan Bearer. El login funcional y el POST de donacion quedan fuera (ver `plan.md` secciones 4 y 5).
- Flujo acordado Opcion 1: invitado ve vidriera (entidades + detalle) y al donar va a `/login`; zona personal requiere identidad via `donanteIdActual`.

## Goals / Non-Goals

**Goals:**

- Conectar vidriera (V1/V2) y zona personal (V3/V4) contra los DTOs de vista existentes sin tocar CSS, con `errorCarga` + reintento ante backend caido.
- Fijar el mapper `Response -> VistaDto` como unico lugar de derivaciones (titulo, porcentaje, labels de estado, fallbacks), como clases puras testeables.
- Dejar mapa (V5) con fallback ilustrativo + spike registrado, sin bloquear el resto.

**Non-Goals:**

- No se implementa login/registro, `autenticacion-service`, ni el POST de donacion.
- No se agrega geocoding ni libreria de mapas en el front.
- No se modifican endpoints existentes del `donaciones-service` (solo se propone agregar campos).

## Decisions

### D1. `WebClient` directo en todas las vistas, con helper privado por servicio

Todas las llamadas (invitado y logueado) usan `WebClient` directo con `.timeout(...)` antes de `.block()`. Cada `ApiService` encapsula su llamada en un helper privado para que el swap a `WebApiCallerService` sea 1 linea cuando exista auth. Razon: no hay `session.accessToken`, y el Bearer hoy no se valida en el back. **Alternativa descartada:** `WebApiCallerService` desde el inicio; revienta sin token y no aporta seguridad real.

### D2. Mappers puros en `mapper/`, una linea por handler en el controller

Cada `back_service` tiene `mapper/` con clases puras (sin Spring) y test en `front-end-app/src/test`; expone metodos que devuelven VistaDto ya mapeado (`obtenerTodas`, `obtenerPorId`, `obtenerPorDonante`, `obtenerIncentivos`). El controller cambia una linea por handler y captura `BackendNoDisponibleException` seteando `errorCarga`. Cada hito toca solo su handler, su metodo, su mapper y su template (anti-conflicto, ver `plan.md` C7). **Alternativa descartada:** mapear en el controller o en Thymeleaf; dispersa porcentaje/labels y rompe que el porcentaje se calcula en el DTO, no en el HTML.

### D3. Derivaciones del mapper donacion (V3, implementado)

- `titulo`: `bienes[0].cantidad + " " + bienes[0].nombre` -> `descripcionGeneral` -> `"Donacion #" + id`.
- `categoria`/`subcategoria`: `subcategoria.categoriaNombre` / `subcategoria.nombre`; `null` -> `"Sin categoria"`.
- `estado` label: tabla fija de 7 (`EN_TRASLADO`->"En transporte", `ENTREGADA`->"Entregada", etc.); `ESTADOS_DISPONIBLES` puebla el filtro independiente de los datos. Enfasis CSS via campo `claseEstado` (`activo` para los 3 en ruta, `exito` para entregada, `""` resto); el template ya no compara literales.
- `destacado`: solo `EN_TRASLADO` (boton "Ver mapa" con `th:if`); `imagenUrl`: siempre `null` (`BienResponse` sin foto; el template omite `img`).
- **Alternativa descartada:** exponer el enum crudo y mapear en Thymeleaf; acopla 7 valores al HTML y duplica logica.

### D4. Derivaciones del mapper entidad + fallback de direccion (V1/V2, implementado)

- `metricas`: una por `NecesidadDto` con guarda div/0 y nulls, `critica` si cobertura < 30% o extraordinaria **inferida por `periodoRecurrencia == null`** (`tipoNecesidad` llega serializado como `Clase@hash`, verificado contra back real; si el back expone el enum, el chequeo por nombre lo toma); progreso = promedio ponderado (0 sin necesidades); `claseColorProgreso` `"alerta"` si alguna critica; resumen del modal con criticas primero.
- `ubicacion`: `direccion.calle + " " + altura + ", " + ciudad` (partes presentes); sin `direccion` -> linea omitida (sin fallback desde contactos); `descripcion` -> `"Descripcion pendiente"`; `imagenUrl` -> `null`; `impacto` `0` + icono por tabla segun `tipo` (default `fa-hand-holding-heart`).
- Detalle (`EntidadDetalleMapper`, helpers compartidos en `EntidadMapperSupport`): categorias agrupadas por tipo inferido (extraordinarias primero, luego recurrentes; grupos vacios se omiten); `frecuencia` etiqueta unica del grupo (`P1M`->"Mensual", `P1Y`->"Anual", mezcla/otro -> `null`, extraordinarias siempre `null`); insumo con `urgente`, `completado` (donada >= requerida) y `claseColorBarra` green/red/teal; `verificada` `false`; impacto `List.of()`.
- Helpers `(requerida, donada)` en ese orden (el orden inverso produjo un swap silencioso de criticidad; los tests lo fijan).
- **Alternativa descartada:** bloquear la vidriera hasta que el backend agregue campos; el fallback es legible y el agregado queda como prerequisito paralelo (ver `plan.md` 4).

### D4b. Toda vista sobre `layout/base` define `scriptsExtra` (aunque vacio)

Sin el bloque, `layout/base` (linea 7, `~{:: scriptsExtra ?: ~{}}`) responde 500 ante backend caido en `verNecesidadEntidad` (verificado). Las demas vistas ya lo definen; `mapaDonaciones` tampoco lo tiene (pendiente en V5: hoy no falla porque no llama al back).

### D5. Donar -> /login manual en modo invitado, sin tocar CSS

Redirect manual `return "redirect:/login"` en handlers personales (el entry point de `SecurityConfig` nunca se dispara bajo `permitAll`); `esInvitado` expuesto con `@ModelAttribute` (los templates no leen `session`); `/login` muestra su pagina actual (vacia, aceptado). En V1 el `.botonDonarAhora` pasa a `@{/login}` si `esInvitado` y el `.modalContenedor` se renderiza solo logueado; logueado abre el modal informativo como hoy. En V2 `.botonDonarCategoria` igual y `.formularioDonacion` queda `type="button"` sin POST. **Alternativa descartada:** modal intermedio "inicia sesion"; duplica el mecanismo `:target`.

### D6. Identidad minima: `donanteIdActual` + property demo

`IdentidadService.donanteIdActual(session)` -> `session.donanteId` o property `app.donante-demo-id` (vacia por defecto); `null` = invitado; `esInvitado(session)` lo niega. Con la property vacia se prueba invitado, seteada se desarrolla V3/V4. **Alternativa descartada:** endpoint `/me` en este change; requiere disenar auth completo.

### D7. Mapa e incentivos: merge/spike segun relevamiento

- Incentivos (V4, implementado): `IncentivosMapper.combinar(...)` mezcla misiones completadas e insignias reales con fallback ilustrativo (categorias, en-progreso/bloqueadas, bloqueadas); `soloFallback()` ante backend caido + `errorCarga`.
- Mapa (V5, pendiente): fallback ilustrativo rotulado; spike entrega tabla endpoint->DTO + propuesta de endpoint faltante + decision pines fijos (max 3) vs dinamica.
- **Alternativa descartada:** conectar a ciegas contra DTOs no relevados.

## Risks / Trade-offs

- **[R1] `EntidadBeneficiariaResponse` sin direccion/descripcion/imagen.** Vidriera con fallback. → Mitigacion D4 + prerequisito backend (ver `plan.md` 4).
- **[R2] Sin login no hay identidad real.** V3/V4 dependen de demo id. → Mitigacion D6.
- **[R3] Labels derivados divergen del back o del template.** Clase CSS / boton mapa no aparecen. → Tabla unica en el mapper + test; `claseEstado` en vez de literales (V3).
- **[R4] Backend caido.** Pagina en blanco. → Timeout + `BackendNoDisponibleException` + `errorCarga` con reintento; 404 -> `NotFoundException` existente.
- **[R5] Pines fijos max 3.** Cuarto punto se apila. → Aceptado; el spike V5 decide.
- **[R6] Swap a `WebApiCallerService` cuando llegue auth.** Cambio disperso. → Helper privado por servicio (D1).
- **[R7] Conflictos de merge en archivos compartidos.** → Cada hito toca solo lo suyo (D2); Hito 0 mergeado antes.
- **[R8] Incentivos mitad real, mitad ilustrativo.** Solo completadas/obtenidas existen. → Fallback marcado + prerequisito backend (catalogo con progreso, `estadoGeneral`).

### Migration Plan

Sin migracion de datos. Orden: Hito 0 (hecho y mergeado) -> V1/V2 -> V5 spike; V3/V4 hechos. Rollback por hito: revertir handler + template + metodo; los mocks viven en git history. No se toca `donaciones-service` en este change. Compilar con `mvn -q -pl front-end-app -am compile` (agregar `-o` solo con dependencias locales; sin `JAVA_HOME` fijo: el anterior era Linux-only).

## Open Questions

- **¿Filtros por query?** Siguen presentacionales. Con GET + `@RequestParam` seria un change aparte; no altera DTOs.

# Plan centralizado — conectar-persona-donante

Flujo acordado: **Opcion 1**. El donante entra como **invitado** a la vidriera publica y solo al intentar donar se le
pide **login**. Este archivo centraliza el flujo y **divide el trabajo en hitos por vista** para repartir la integracion
con el back entre integrantes. Cada hito de vista depende solo del Hito 0 y toca sus propios archivos.

```
INVITADO                          LOGIN                       LOGUEADO (o demo id)
--------                          -----                       --------------------
main, quienesSomos, etc           /login                      misDonaciones (V3)
entidadesBeneficiarias (V1) --->  (redirect manual,           incentivos (V4)
verNecesidadEntidad/{id} (V2)     form fuera de alcance)      mapa personal (V5, spike)
   boton Donar ------------------> ^
mapa decorativo (V5)               | login OK (change de auth)
                                   +---------------------------+
```

## 1. Reglas comunes (aplican a todas las vistas)

| #  | Regla                                                                                                                                                                                                                       | Por que                                                                                                                                                                                                 |
|----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| C1 | **HTTP:** `WebClient` directo en todas las vistas (invitado y logueado). Cada ApiService encapsula la llamada en un helper privado para que el swap a `WebApiCallerService` sea 1 linea cuando exista auth.                 | `WebApiCallerService.executeWithTokenRetry` lanza `RuntimeException` si no hay `session.accessToken`, y nadie lo escribe. Los servicios del back no tienen Spring Security: el Bearer hoy no se valida. |
| C2 | **Identidad:** helper unico `donanteIdActual(session)` -> `session.donanteId` o, si no hay, la property `app.donante-demo-id` (vacia por defecto). `null` = invitado.                                                       | Una sola fuente; con la property vacia se puede probar el modo invitado, con la property seteada se desarrolla V3/V4.                                                                                   |
| C3 | **Invitado en templates:** `esInvitado` expuesto con `@ModelAttribute` en `PersonaDonanteController`. Los templates no leen `session`.                                                                                      | Logica fuera del HTML.                                                                                                                                                                                  |
| C4 | **Redirect a login manual:** `return "redirect:/login"` en los handlers personales.                                                                                                                                         | `SecurityConfig` esta en `anyRequest().permitAll()`: el `authenticationEntryPoint` nunca se dispara. `/login` hoy muestra un `login.html` vacio (aceptado).                                             |
| C5 | **Errores:** `.timeout(Duration.ofSeconds(n))` antes de `.block()`. El service lanza una excepcion de carga propia; el controller la captura y setea `errorCarga=true` + lista vacia. 404 -> `NotFoundException` existente. | `onErrorReturn(List.of())` no distingue "backend caido" de "no hay datos", y las specs piden mensaje de reintento.                                                                                      |
| C6 | **Mappers** como clases puras (sin Spring) en `services/back_services/<servicio>/mapper/`, con test unitario en `front-end-app/src/test`.                                                                                   | Unico lugar de derivaciones; testeable sin levantar contexto.                                                                                                                                           |
| C7 | **Anti-conflicto:** cada integrante toca solo su handler en `PersonaDonanteController`, su metodo en el ApiService, su mapper y su template. Nadie toca `static/css/`.                                                      | Permite trabajar en paralelo con merges triviales.                                                                                                                                                      |
| C8 | **Compilar:** `mvn -q -pl front-end-app -am compile` (agregar `-o` solo si el repo local ya tiene las dependencias).                                                                                                        | El comando anterior usaba un `JAVA_HOME` de Linux.                                                                                                                                                      |

## 2. Hito 0 — Base compartida (bloquea a todos, chico)

- **Responsable:** `____`
- **Entregables codigo:**
    - Helper de identidad (C2) + property `app.donante-demo-id` en `application.yaml` (vacia).
    - `@ModelAttribute("esInvitado")` en `PersonaDonanteController` (C3).
    - Excepcion de carga (p.ej. `BackendNoDisponibleException` en `exceptions/`) y patron `errorCarga` (C5), con un
      ejemplo aplicado.
- **Entregables OpenSpec:**
    - Mover `proposal.md`, `design.md`, `tasks.md` (y este `plan.md`) a la raiz del change; specs como
      `specs/<capability>/spec.md` (hoy `specs/.../specXxx.md`).
    - Quitar referencias "Hereda" a `openspec/changes/vistas-persona-donante/specs/` (no existe; `archives/` esta vacio)
      o archivar ese change primero.
    - Alinear `design.md` (D1, D2, D5, D6) y `tasks.md` con este plan; sacar el POST de donacion de `proposal.md` y de
      la spec `mis-donaciones`.
- **Terminado cuando:** compila (C8), `openspec validate conectar-persona-donante --strict` pasa, y con la property
  vacia `esInvitado=true` en cualquier vista.

## 3. Hitos por vista

### V1 — Entidades beneficiarias (grilla) — invitado

- **Responsable:** `____` | **Depende de:** Hito 0
- **Back:** `GET /donaciones-service/entidades-beneficiarias` ->
  `List<EntidadBeneficiariaResponse(id, razonSocial, tipo, contactos, representantes, necesidades)>`
- **Archivos:** `EntidadBeneficiariaApiService.obtenerTodas()`, `mapper/EntidadVistaMapper` (nuevo), handler
  `entidadesBeneficiarias`, `templates/personaDonante/entidadesBeneficiarias.html`.
- **Mapeo `EntidadVistaDto`:**

| Campo vista                 | Origen / fallback                                                                                                                                                                                        |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`, `razonSocial`, `tipo` | directo                                                                                                                                                                                                  |
| `ubicacion`                 | `direccion.calle + " " + direccion.altura`; sin `direccion` -> linea omitida (`MedioDeContactoDTO` es `(tipo, valor)`, no sirve de fallback)                                                              |
| `descripcion`               | `null` -> texto "Descripcion pendiente" sin lorem                                                                                                                                                        |
| `imagenUrl`                 | `null` (el template ya omite `img`)                                                                                                                                                                      |
| `metricas`                  | una `MetricaVistaDto` por `NecesidadDto`: nombre = `subcategoria`, `cantidad`/`cantidadDonada` Double->int, `porcentaje` con guarda div/0 y null, `critica` si < 30% o `periodoRecurrencia == null` (inferencia: `tipoNecesidad` llega como `Clase@hash`, ver nota) |
| `progresoPorcentaje`        | promedio ponderado de las necesidades (0 si no hay)                                                                                                                                                      |
| `claseColorProgreso`        | `"alerta"` si alguna critica, sino `""`                                                                                                                                                                  |
| `impacto` / `iconoImpacto`  | `0` / icono por `tipo` (tabla fija en el mapper, default `fa-hand-holding-heart`)                                                                                                                        |
| `necesidades`               | `NecesidadResumenDto` de las criticas primero, `claseColorProgreso="alerta"` si urgente                                                                                                                  |

- **Invitado:** `.botonDonarAhora` -> `@{/login}` si `esInvitado`; el `.modalContenedor` por entidad se renderiza solo
  logueado. Clases CSS intactas.
- **Terminado cuando:** grilla con datos reales; backend caido -> mensaje de reintento; invitado click Donar ->
  `/login`; test del mapper (0%, 100%, critica < 30%, sin necesidades, cantidad 0).

- **Nota `tipoNecesidad` (verificado contra back real 2026-10-14):** el campo llega serializado como
  `Clase@hash` (ej. `...TipoNecesidadRecurrente@1f94888d`), nunca como `"RECURRENTE"`/`"EXTRAORDINARIA"`.
  Por eso la criticidad/grupo se infiere por `periodoRecurrencia == null` -> extraordinaria (la recurrente
  exige periodo en el dominio; la extraordinaria se crea sin el). Cuando el back exponga
  `TipoNecesidadEnum` (prerequisito abajo), el mapper cambia una linea.

### V2 — Detalle de entidad (`verNecesidadEntidad/{id}`) — invitado

- **Responsable:** `____` | **Depende de:** Hito 0 (comparte `EntidadBeneficiariaApiService` con V1: cada uno agrega
  solo su metodo)
- **Back:** `GET /donaciones-service/entidades-beneficiarias/{id}` (404 via `GlobalExceptionHandler`)
- **Archivos:** `EntidadBeneficiariaApiService.obtenerPorId(id)`, `mapper/EntidadDetalleMapper` (nuevo), handler
  `verNecesidadEntidad`, `templates/personaDonante/verNecesidadEntidad.html`.
- **Mapeo:**

| Campo vista                               | Origen / fallback                                                                                                                                                                                                                                                                                                           |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `EntidadDetalleDto.id/razonSocial/tipo`   | directo                                                                                                                                                                                                                                                                                                                     |
| `ubicacion`, `descripcion`, `imagenUrl`   | igual que V1                                                                                                                                                                                                                                                                                                                |
| `verificada`                              | `false` (no existe en back)                                                                                                                                                                                                                                                                                                 |
| `impacto: List<CifraImpactoDto>`          | `List.of()`; verificar que el template no deje un bloque vacio                                                                                                                                                                                                                                                              |
| `categorias: List<CategoriaNecesidadDto>` | **agrupadas por `tipoNecesidad`** (RECURRENTE / EXTRAORDINARIA): titulo y descripcion fijos por tipo, icono/color por tipo. `NecesidadDto` trae `subcategoria` como String, sin categoria. Alternativa si se quiere agrupar por categoria real: lookup con `GET /donaciones-service/subcategorias` (decidir al implementar) |
| `CategoriaNecesidadDto.frecuencia`        | desde `periodoRecurrencia` (`P1M`->"Mensual", `P1Y`->"Anual", otro -> `null`)                                                                                                                                                                                                                                               |
| `InsumoNecesidadDto`                      | nombre = `subcategoria`, cantidades Double, `porcentaje` con guarda, `urgente` = regla de critica de V1, `completado` = donada >= requerida, `claseColorBarra` red/teal/green segun urgente/normal/completado                                                                                                               |

- **Invitado:** `.botonDonarCategoria` -> `@{/login}` si `esInvitado`. El `.formularioDonacion` queda como hoy (
  `type="button"`, sin POST): el POST esta fuera de alcance (ver seccion 5).
- **Terminado cuando:** `/1` renderiza datos reales; id inexistente -> mensaje not-found con link a entidades; backend
  caido -> reintento; test del mapper (agrupamiento, frecuencia, completado).

### V3 — Mis donaciones — logueado

- **Responsable:** `____` | **Depende de:** Hito 0
- **Back:** `GET /donaciones-service/donaciones/donante/{idDonante}` ->
  `List<DonacionResponse(id, bienes, descripcionGeneral, estado, ultimaActualizacion, subcategoria, ...)>`
- **Archivos:** `DonacionApiService.obtenerPorDonante(id)`, `mapper/DonacionMapper` (nuevo), handler `misDonaciones`,
  `templates/personaDonante/misDonaciones.html` (solo si se agrega `claseEstado`).
- **Mapeo `DonacionVistaDto`:**

| Campo vista                  | Origen / fallback                                                                                                                                                                                                                        |
|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `titulo`                     | `bienes[0].cantidad + " " + bienes[0].nombre` -> `descripcionGeneral` -> `"Donacion #" + id`                                                                                                                                             |
| `categoria` / `subcategoria` | `subcategoria.categoriaNombre` / `subcategoria.nombre`; `subcategoria` puede ser `null` -> `"Sin categoria"`                                                                                                                             |
| `descripcion`                | `descripcionGeneral`                                                                                                                                                                                                                     |
| `estado`                     | tabla: `DEPOSITO`->"En deposito", `ASIGNACION_REALIZADA`->"Asignada", `LISTA_PARA_ENTREGAR`->"Lista para entregar", `EN_TRASLADO`->"En transporte", `ENTREGADA`->"Entregada", `ENTREGA_FALLIDA`->"Entrega fallida", `VENCIDO`->"Vencida" |
| `imagenUrl`                  | `null` (`BienResponse` no trae foto)                                                                                                                                                                                                     |
| `destacado`                  | `estado == EN_TRASLADO`                                                                                                                                                                                                                  |

- **Atencion template:** `misDonaciones.html` compara `donacion.estado == 'En transporte'` / `'Entregada'` para la clase
  CSS y el boton "Ver mapa". O se respetan esos strings exactos (y el test los fija), o se agrega `claseEstado` al DTO y
  se usa en el template (sin tocar CSS).
- **Filtros:** `estados` sale de la misma tabla del mapper (el mock tenia "Esperando asignacion", que no existe);
  `categorias`/`subcategorias` = valores distintos de las donaciones cargadas. Siguen siendo presentacionales.
- **Invitado:** `donanteIdActual == null` -> `redirect:/login`.
- **Terminado cuando:** con demo id renderiza datos reales; sin demo id redirige; backend caido -> reintento; test del
  mapper (7 estados, titulo con y sin bienes, subcategoria null).

### V4 — Incentivos — logueado

- **Responsable:** `____` | **Depende de:** Hito 0
- **Back (`incentivos-service`, `:8081`, sin prefijo `/incentivos-service`):**
    - `GET /incentivos/{id}/misiones` -> `List<MisionResponse>` (**solo misiones completadas**)
    - `GET /incentivos/{id}/insignias` -> `List<InsigniaResponse>` (insignias del donante)
    - `GET /incentivos/{id}/metricas` -> `MetricasUsuarioResponse`
    - `GET /incentivos/{id}/estadoGeneral` -> `void` (stub, no sirve todavia)
- **Archivos:** `IncentivosApiService.obtenerIncentivos(donanteId)`, `mapper/IncentivosMapper` (combina real +
  fallback), handler `incentivos`. Tabla de mapeo completa en `tasks.md` seccion 4.
- **Relevamiento (hecho):** `MisionResponse(insignia, fecha)` e `InsigniaResponse(descripcion)` son records chicos sin
  estado/titulo/nombre/icono propios; `/misiones` ya filtra solo completadas en el back (no es una suposicion).
  `MetricasUsuarioResponse` no aporta nada util para esta vista (ranking, historico, comparacion de meses — nada de
  categoria/misiones/insignias totales). Confirmado por codigo (no supuesto): `{id}` es el mismo `idDonante` que
  `donaciones-service` (el alta de donante en `incentivos-service` lo fija explicitamente, no lo autogenera).
- **Gaps conocidos:** no hay catalogo de misiones con progreso (solo completadas) ni categoria del donante (
  `estadoGeneral` vacio, confirmado stub). `IncentivosMapper` combina las misiones/insignias reales con el contenido
  ilustrativo original para lo que el back no da (categorias, progreso, catalogo de bloqueadas), marcado explicitamente
  en el codigo; si hace falta un endpoint nuevo, va a prerequisitos backend (seccion 4).
- **Invitado:** `redirect:/login`.
- **Terminado:** con demo id muestra insignias y misiones completadas reales (resto fallback); sin demo id redirige;
  backend caido -> fallback completo + mensaje de reintento; tabla de mapeo documentada en `tasks.md`; test unitario del
  mapper (`IncentivosMapperTest`).

### V5 — Mapa de donaciones — spike + fallback

- **Responsable:** `____` | **Depende de:** Hito 0 (solo para el redirect si se decide personal)
- **Estado del back:** `logistica-service` no tiene consulta por donante. Expone `GET /logistica-service/rutas`,
  `/rutas/{id}`, `/entregas/pendientes`, `/entregas/{id}`.
- **Fallback (este change):** la vista queda con el mock actual, rotulada como ilustrativa (tiempos incluidos). No se
  arman puntos cercanos desde entidades: no hay direccion ni horario para `PuntoCercanoDto`.
- **Spike (completo 2026-10-14):** `logistica-service` no exige auth (`VerificadorDeAutenticacion` vacio,
  igual que `donaciones-service`). Sin filtro por donante: el front debe cruzar.
  1. `GET /donaciones-service/donaciones/donante/{id}` -> ids de donaciones del donante.
  2. `GET /logistica-service/entregas/pendientes` -> `List<EntregaResponse>`; filtrar las que contengan
     esos ids en `donacionesIds`.
  3. `GET /logistica-service/rutas` -> `List<RutaResponse>`; ubicar la ruta que contenga esas entregas
     en `paradas[].entrega`.

  | Dato vista | Origen | Gap |
  |------------|--------|-----|
  | `PinMapaDto.nombre` | `EntregaResponse.direccionEntidad` | ninguno |
  | `PinMapaDto.etiquetaEstado` | `EntregaResponse.estadoActual` / `RutaResponse.estadoActual` | mapear labels como en V3 |
  | `PinMapaDto.tiempoEstimado` | DERIVADO (`ParadaResponse.fechaEntrega` o `RutaResponse.fechaFinDeViaje` menos ahora) | **no hay ETA explicito**: proponer campo o regla |
  | `PinMapaDto.enlace` | `/personaDonante/entidadesBeneficiarias` (sin id de entidad en la entrega) | sin vinculo entrega->entidad |
  | `PuntoCercanoDto.direccion` | `direccionEntidad` | ninguno |
  | `PuntoCercanoDto.horario` | — | **no existe en el back** |
  | `PuntoCercanoDto.tiempoEstimado` | idem pin | idem pin |
  | `donacionEnRuta` | entrega del donante en ruta + `DonacionResponse` para titulo | ninguno |
- **Propuesta de endpoint faltante (prerequisito logistica):**
  `GET /logistica-service/entregas?donanteId={id}` (o `.../rutas?donanteId=`) con `tiempoEstimado` y
  `horario` explicitos. Sin el, la conexion igual es posible filtrando en el front (escala chica),
  pero fea.
- **Decision pines:** primera conexion con los 3 roles fijos del CSS (origen/camion/destino mapeados a las
  clases existentes); mapa dinamico con libreria queda para un change propio (hay `latitud`/`longitud`
  en `EntregaResponse` para cuando llegue).
- **Nota:** `auth.service.url` y `logistica.service.url` apuntan ambos a `localhost:8083` en `application.yaml`; revisar
  al conectar.
- **Terminado cuando:** la vista renderiza sin errores con el rotulo, y la tabla del spike esta agregada en este
  archivo.

## 4. Prerequisitos backend (PR separados, fuera de este change)

| Prerequisito                                                                                                                    | Servicio                      | Desbloquea                                        |
|---------------------------------------------------------------------------------------------------------------------------------|-------------------------------|---------------------------------------------------|
| `direccion` parcial: `descripcion`, `imagenUrl`, `ciudad` en `EntidadBeneficiariaResponse` (`calle`+`altura` ya llegan) | donaciones-service            | V1/V2 sin fallback                                |
| Exponer `TipoNecesidadEnum` (`getTipoNecesidad().name()`) en `NecesidadDto` en vez del objeto serializado como `Clase@hash`   | donaciones-service            | V1/V2 clasifican sin inferir                      |
| Endpoint de rutas/entregas por donante con tiempo estimado                                                                      | logistica-service             | V5 personal                                       |
| Catalogo de misiones con progreso + categoria del donante (`estadoGeneral`)                                                     | incentivos-service            | V4 completo                                       |
| Auth: `POST /auth/login`, `POST /auth/refresh`, `CustomAuthProvider` real, escribir `session.accessToken` y `session.donanteId` | autenticacion-service + front | Quitar demo id; swap a `WebApiCallerService` (C1) |

## 5. Fuera de alcance

- **POST de donacion** (change propio). Hoy no se puede armar: el form tiene `cantidad`/`mensaje`/`contacto` pero
  `BienRequest` exige `nombre`, `unidadDeMedida`, `subcategoriaId`, etc.; ni `NecesidadDto` ni `InsumoNecesidadDto`
  traen `subcategoriaId`; `CrearDonacionRequest` no lleva entidad ni necesidad destino (las asigna el matcheador); y el
  endpoint responde `List<DonacionResponse>`. Requiere: `subcategoriaId` en el back, form por insumo con campos ocultos,
  `@PostMapping` + PRG.
- Login/registro funcional, filtros que filtran de verdad, mapa con libreria, cambios en `static/css/`.

## 6. Riesgos

| Riesgo                                                                                                       | Impacto                            | Mitigacion                                                       | Hito   |
|--------------------------------------------------------------------------------------------------------------|------------------------------------|------------------------------------------------------------------|--------|
| R1 `EntidadBeneficiariaResponse` sin `direccion`/`descripcion`/`imagenUrl`                                   | Cards y detalle incompletos        | Fallback legible + prerequisito backend                          | V1, V2 |
| R2 Sin login no hay identidad                                                                                | V3/V4 no se pueden probar          | Property `app.donante-demo-id` (C2)                              | Hito 0 |
| R3 Labels derivados divergen del back o del template                                                         | Clase CSS / boton mapa no aparecen | Tabla unica en el mapper + test; o `claseEstado`                 | V3     |
| R4 Backend caido                                                                                             | Pagina en blanco                   | Timeout + `errorCarga` (C5)                                      | Todos  |
| R5 Pines fijos max 3                                                                                         | 4to punto se apila                 | Aceptado; el spike decide                                        | V5     |
| R6 Swap a `WebApiCallerService` cuando llegue auth                                                           | Cambio disperso                    | Helper privado por ApiService (C1)                               | Todos  |
| R7 Conflictos de merge en archivos compartidos (`PersonaDonanteController`, `EntidadBeneficiariaApiService`) | Merges con conflictos              | Cada uno toca solo su handler/metodo (C7); Hito 0 mergeado antes | Todos  |
| R8 Incentivos solo devuelve misiones completadas                                                             | Vista mitad real, mitad mock       | Fallback marcado + prerequisito backend                          | V4     |

## 7. Reparto

| Hito | Vista                      | Responsable | Depende de | Back               | Estado    |
|------|----------------------------|-------------|------------|--------------------|-----------|
| 0    | Base compartida + OpenSpec | `____`      | —          | —                  | hecho     |
| V1   | Entidades beneficiarias    | `____`      | 0          | donaciones-service | hecho     |
| V2   | Detalle de entidad         | `____`      | 0          | donaciones-service | hecho     |
| V3   | Mis donaciones             | `____`      | 0          | donaciones-service | hecho     |
| V4   | Incentivos                 | `____`      | 0          | incentivos-service | hecho     |
| V5   | Mapa (spike)               | `____`      | 0          | logistica-service  | hecho     |

Orden: Hito 0 primero y mergeado; despues V1–V5 en paralelo.

## 8. Verificacion integral

1. Compilar (C8) y correr los tests de mappers.
2. Con `app.donante-demo-id` vacia: `entidadesBeneficiarias` -> `verNecesidadEntidad/{id}` -> click Donar -> `/login`;
   `misDonaciones` e `incentivos` redirigen a `/login`.
3. Con `app.donante-demo-id` seteada: `misDonaciones` e `incentivos` renderizan datos reales (con fallbacks donde
   corresponda).
4. Con el backend apagado: cada vista muestra mensaje de reintento sin error de template.
5. `git status` sin cambios bajo `static/css/`.
6. `openspec validate conectar-persona-donante --strict`.

## 9. Avance real (hitos completados)

Registro vivo de lo que ya esta hecho y mergeable, para no depender de memoria entre sesiones. Se actualiza al cerrar
cada hito.

### Hito 0 — Base compartida (completo)

- **Codigo:**
    - `IdentidadService.donanteIdActual(session)` / `.esInvitado(session)` (C2), respaldado por la property
      `app.donante-demo-id` (vacia por defecto) en `application.yaml`.
    - `@ModelAttribute("esInvitado")` en `PersonaDonanteController` (C3).
    - `BackendNoDisponibleException` en `exceptions/` (C5), con el patron `errorCarga` aplicado de punta a punta en
      `DonanteApiService` + `AccesoController` + `acceso/main.html` como ejemplo de referencia para el resto de los
      hitos.
- **OpenSpec:** `proposal.md`/`design.md`/`tasks.md`/`plan.md` movidos a la raiz del change;
  `specs/<capability>/specXxx.md` renombrados a `specs/<capability>/spec.md`; referencias "Hereda" a
  `vistas-persona-donante` eliminadas; `design.md` (D1, D2, D5, D6) y `tasks.md` realineados con este plan; POST de
  donacion sacado de `proposal.md` y de la spec `mis-donaciones`.
- **Decision registrada:** C1 se interpreta como "`WebClient` directo + helper privado por ahora" en vez de
  `WebApiCallerService` desde el inicio, porque todavia no existe `session.accessToken`; el swap queda aislado a una
  linea por servicio.

### V3 — Mis donaciones (completo)

- **Feature:** `/personaDonante/misDonaciones` conectada a `GET /donaciones-service/donaciones/donante/{id}`; invitados
  redirigen a `/login`; backend caido -> `errorCarga` + mensaje de reintento (sin romper el template).
- **Codigo nuevo:** DTOs espejo del backend (`DonacionResponse`, `BienResponse`, `SubcategoriaResponse`,
  `TipoDeEstado`), `DonacionMapper` (puro, con `ESTADOS_DISPONIBLES`), `DonacionApiService.obtenerPorDonante(id)`.
- **Cambio de diseno sobre el plan original:** se agrego el campo `claseEstado` a `DonacionVistaDto` (no estaba
  previsto) porque el template solo comparaba el literal `'En transporte'`/`'Entregada'` inline, y la spec exige enfasis
  visual para los 3 estados "en ruta" (`EN_TRASLADO`, `LISTA_PARA_ENTREGAR`, `ASIGNACION_REALIZADA`), no solo uno. El
  boton "Ver mapa" pasa de comparar el string de estado a usar `donacion.destacado`. Sin tocar `static/css/` (clases CSS
  reutilizadas).
- **Filtros:** `estados` sale de `DonacionMapper.ESTADOS_DISPONIBLES` (7 labels fijos); `categorias`/`subcategorias` son
  los valores distintos de las donaciones cargadas.
- **Tests:** `DonacionMapperTest` (13 casos: 7 estados, titulo con/sin bienes, subcategoria null, imagen siempre null).

### V4 — Incentivos (completo)

- **Feature:** `/personaDonante/incentivos` conectada a `GET /incentivos/{id}/misiones` y
  `GET /incentivos/{id}/insignias` de `incentivos-service`; invitados redirigen a `/login`; backend caido -> fallback
  ilustrativo completo + `errorCarga`.
- **Codigo nuevo:** DTOs espejo (`MisionResponse`, `InsigniaResponse`, reutilizado tambien para el campo anidado
  `insignia` de `MisionResponse`), `IncentivosVistaDto` (bundle de todo lo que necesita el template),
  `IncentivosMapper.combinar(...)` / `.soloFallback()`, `IncentivosApiService.obtenerIncentivos(donanteId)`.
- **Gap de backend confirmado por codigo (no supuesto):** `incentivos-service` solo expone misiones completadas (sin
  titulo/categoria/progreso propios) e insignias obtenidas (sin nombre/icono/color propios); `estadoGeneral` (de donde
  saldria la categoria del donante) es un stub vacio. `MetricasUsuarioResponse` no aporta nada util para esta vista.
- **Decision de diseno:** `IncentivosMapper` combina misiones/insignias **reales** del backend con el contenido
  ilustrativo original para todo lo que el backend no da (categoria actual/siguiente, misiones en progreso/bloqueadas,
  catalogo de insignias bloqueadas), en vez de mockear todo o conectar a ciegas. Confirmado tambien que `{id}` en las
  rutas de incentivos es el mismo `idDonante` de `donaciones-service`.
- **Tests:** `IncentivosMapperTest` (5 casos: fallback puro, merge de mision real, merge de insignia real, mision sin
  insignia asociada, `soloFallback()` reproduce el mock original).

### V1 — Grilla (completo)

- `EntidadBeneficiariaApiService.obtenerTodas()` + `EntidadVistaMapper` + swap del handler con
  `errorCarga`; Donar -> `/login` si invitado, modal solo logueado. Ubicacion compuesta `"calle altura"`
  e inferencia de extraordinaria agregadas despues (tareas 1.4/1.5).

### V2 — Detalle (completo)

- `EntidadBeneficiariaApiService.obtenerPorId(id)` (404 -> `NotFoundException` via `onStatus`) +
  `EntidadDetalleMapper` (grupos extraordinarias/recurrentes, frecuencia, `completado`, colores;
  helpers compartidos en `EntidadMapperSupport`) + swap del handler (`errorCarga`, `noEncontrada`) +
  template (Donar -> `/login` si invitado, modal solo logueado, bloque `scriptsExtra` vacio: sin el,
  `layout/base` responde 500 ante backend caido).
- Tests: `EntidadDetalleMapperTest` (12 casos). Verificado en navegador contra back real (`/19` real,
  `/9999` not-found, reintento sin back) en modo invitado y logueado.

### V5 — Mapa (completo: fallback + spike)

- Fallback rotulado como ilustrativo + bloque `scriptsExtra` preventivo. Spike en §V5: tabla
  endpoint->DTO con `EntregaResponse.donacionesIds` como join key, gaps (sin ETA explicito, sin horario,
  sin vinculo entrega->entidad) y propuesta de `GET /entregas?donanteId=`; decision: 3 roles fijos primero.

### Pendiente (no tocado todavia)

- Prerequisitos de backend de la seccion 4 (`TipoNecesidadEnum` en `NecesidadDto`, endpoint logistica por
  donante, catalogo/`estadoGeneral` en incentivos, auth real). Siguen vigentes.
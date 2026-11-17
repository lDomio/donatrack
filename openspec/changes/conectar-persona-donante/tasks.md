## 0. Base compartida — Hito 0 (hecho)

- [x] 0.1 Crear `IdentidadService.donanteIdActual(session)` / `.esInvitado(session)` con property `app.donante-demo-id` (vacia por defecto) y verificar compilacion y que con la property vacia `esInvitado=true`
- [x] 0.2 Exponer `@ModelAttribute("esInvitado")` en `PersonaDonanteController` y verificar que los templates lo reciben sin leer la sesion
- [x] 0.3 Crear `BackendNoDisponibleException` en `exceptions/` con patron `errorCarga` + mensaje de reintento (ejemplo aplicado en `DonanteApiService` + `AccesoController` + `acceso/main.html`) y verificar compilacion y vista con backend apagado
- [x] 0.4 Renombrar specs a `specs/<capability>/spec.md`, alinear `proposal.md`/`design.md`/`tasks.md` con `plan.md` y verificar `openspec validate conectar-persona-donante --strict`

## 1. Vidriera publica — V1 grilla de entidades (1.1–1.3 hechas; 1.4–1.5 pendientes)

- [x] 1.1 Agregar `EntidadBeneficiariaApiService.obtenerTodas()` con `WebClient` directo a `GET /donaciones-service/entidades-beneficiarias` (helper privado + `.timeout(...)` + `BackendNoDisponibleException`) y verificar con `mvn -q -pl front-end-app -am compile`
- [x] 1.2 Implementar `mapper/EntidadVistaMapper` puro segun design D4 (guarda div/0 y nulls, critica < 30% o EXTRAORDINARIA, progreso ponderado, ubicacion omitida, "Descripcion pendiente", impacto 0 + icono por tipo, criticas primero) y verificar con test unitario (`EntidadVistaMapperTest`: 15 casos)
- [x] 1.3 Reemplazar el mock del handler `entidadesBeneficiarias` por `obtenerTodas()` con `errorCarga`, y `.botonDonarAhora` a `@{/login}` si `esInvitado` (modal solo logueado, clases CSS intactas), y verificar en navegador datos reales, reintento con backend caido y Donar -> `/login`
- [x] 1.4 Componer ubicacion `"calle altura"` desde `direccion` en `EntidadVistaMapper` (linea omitida solo si no hay direccion) y verificar con test (con direccion / sin direccion) y en navegador contra datos reales
- [x] 1.5 Inferir extraordinaria por `periodoRecurrencia == null` en `esCritica` de V1 (`tipoNecesidad` llega como `Clase@hash`, nunca matchea) y verificar con test (EXTRAORDINARIA completa sigue critica; RECURRENTE completa no)

## 2. Vidriera publica — V2 detalle de entidad (pendiente, depende de Hito 0)

- [x] 2.1 Agregar `EntidadBeneficiariaApiService.obtenerPorId(id)` a `GET .../entidades-beneficiarias/{id}` (mismo patron de errores; 404 -> `NotFoundException`) y verificar la misma compilacion
- [x] 2.2 Implementar `mapper/EntidadDetalleMapper` puro segun design D4 (agrupar por `tipoNecesidad` inferido por periodo, frecuencia P1M/P1Y, `completado`, colores red/teal/green, `verificada` false, impacto vacio) y verificar con test unitario (`EntidadDetalleMapperTest`: 12 casos)
- [x] 2.3 Reemplazar el mock del handler `verNecesidadEntidad` por `obtenerPorId(id)` con `errorCarga`, y `.botonDonarCategoria` a `@{/login}` si `esInvitado` (formulario sin POST), y verificar que `/19` renderiza real, id inexistente muestra not-found con link y backend caido muestra reintento (requirio agregar bloque `scriptsExtra` vacio: sin el, `layout/base` 500ea con backend caido)

## 3. Zona personal — V3 mis donaciones (hecho)

- [x] 3.1 Agregar `DonacionApiService.obtenerPorDonante(id)` con `WebClient` directo a `GET /donaciones-service/donaciones/donante/{id}` y verificar compilacion
- [x] 3.2 Implementar `mapper/DonacionMapper` puro segun design D3 (cadena de titulo, "Sin categoria", tabla de 7 labels + `ESTADOS_DISPONIBLES`, `claseEstado`, `destacado` solo EN_TRASLADO, imagen `null`) y verificar con `DonacionMapperTest` (13 casos)
- [x] 3.3 Reemplazar el mock del handler `misDonaciones` por `obtenerPorDonante(donanteId)` con `redirect:/login` si `null`, `errorCarga` y filtros (estados fijos + distintas cargadas), y verificar datos reales, redirect invitado y reintento

## 4. Zona personal — V4 incentivos (hecho)

Contrato relevado de `incentivos-service` (`:8081`, sin prefijo; `{id}` = `idDonante`):

| Endpoint | Responde | Uso en la vista |
|----------|----------|-----------------|
| `GET /incentivos/{id}/misiones` | `List<MisionResponse(insignia, fecha)>` — solo completadas | Misiones completadas reales |
| `GET /incentivos/{id}/insignias` | `List<InsigniaResponse(descripcion)>` — solo obtenidas | Insignias obtenidas reales |
| `GET /incentivos/{id}/metricas` | `MetricasUsuarioResponse` (ranking, historico) | Sin uso en la vista |
| `GET /incentivos/{id}/estadoGeneral` | `void` (stub) | Sin uso; categoria queda fallback |

- [x] 4.1 Agregar `IncentivosApiService.obtenerIncentivos(donanteId)` (misiones + insignias, helper privado + timeout + `BackendNoDisponibleException`) y verificar compilacion
- [x] 4.2 Implementar `mapper/IncentivosMapper.combinar(...)` / `.soloFallback()` (reales + fallback ilustrativo para categoria, progreso, en-progreso/bloqueadas) y verificar con `IncentivosMapperTest` (5 casos)
- [x] 4.3 Reemplazar el mock del handler `incentivos` por `obtenerIncentivos` con `redirect:/login` si `null` y `errorCarga` (fallback completo), y verificar datos reales, redirect invitado y reintento

## 5. Mapa de donaciones — V5 spike + fallback (pendiente)

- [x] 5.1 Dejar `/personaDonante/mapaDonaciones` con el mock actual rotulado como ilustrativo (nota visible; sin puntos derivados de entidades) + bloque `scriptsExtra` vacio preventivo, y verificar que renderiza sin errores
- [x] 5.2 Spike logistica (`GET /logistica-service/rutas`, `/rutas/{id}`, `/entregas/pendientes`, `/entregas/{id}`; sin consulta por donante): entregar tabla endpoint -> `PinMapaDto`/`PuntoCercanoDto` + propuesta del endpoint faltante + decision pines fijos vs dinamica en `plan.md`, y verificar revisando la tabla agregada

## 6. Verificacion integral

- [x] 6.1 Compilar con `mvn -q -pl front-end-app -am compile` (agregar `-o` solo con dependencias locales) y correr los tests de mappers, confirmando cero errores (suite completa: 60/60)
- [x] 6.2 Con `DONANTE_DEMO_ID` vacio explicito (el default de `application.yaml` es `1`) recorrer `entidadesBeneficiarias` -> `verNecesidadEntidad/{id}` -> Donar -> `/login`, y confirmar que `misDonaciones` e `incentivos` redirigen a `/login`
- [x] 6.3 Con `DONANTE_DEMO_ID=1` confirmar que `misDonaciones` e `incentivos` renderizan datos reales (con fallbacks donde corresponda)
- [x] 6.4 Con cada backend apagado confirmar mensaje de reintento sin error de template en cada vista
- [x] 6.5 Confirmar que no se modifico ningun archivo bajo `static/css/` con `git status` (el unico hunk, `.insigniaOng`, es de un companero sin commitear; este change no toca CSS) ni `donaciones-service/` (limpio)
- [x] 6.6 Correr `openspec validate conectar-persona-donante --strict` y corregir hallazgos

## Why

Las 4 vistas de persona donante renderizan con mocks (`List.of(...)` en `PersonaDonanteController`) desde el change `vistas-persona-donante`. El flujo acordado es Opcion 1: el donante entra como invitado a una vidriera publica (entidades + detalle) y solo al intentar donar se le pide login; la zona personal (mis donaciones, mapa personal, incentivos) requiere identidad. El trabajo se reparte en hitos por vista (ver `plan.md`): Hito 0 (base compartida), V1-V2 (vidriera, `donaciones-service`), V3-V4 (personal: donaciones e incentivos, ya conectados), V5 (mapa, spike). Sin esta conexion el area de donante no muestra datos reales.

## What Changes

- Conectar `entidadesBeneficiarias` y `verNecesidadEntidad/{id}` a `GET /donaciones-service/entidades-beneficiarias` y `GET .../{id}` en modo invitado (`WebClient` directo, sin token), con mapper `EntidadBeneficiariaResponse`/`NecesidadDto` -> DTOs de vista.
- Cambiar los controles "Donar ahora" / "Donar a esta categoria" en modo invitado para que naveguen a `/login` con `return "redirect:/login"` manual (driven por el atributo `esInvitado`; los templates no leen la sesion), sin cambiar clases CSS.
- Conectar `misDonaciones` a `GET /donaciones-service/donaciones/donante/{idDonante}` para el donante identificado via `donanteIdActual` (sesion o property `app.donante-demo-id`), con mapper `DonacionResponse` -> `DonacionVistaDto`.
- Conectar `incentivos` a `GET /incentivos/{id}/misiones` y `GET /incentivos/{id}/insignias` (`{id}` = `idDonante`), combinando misiones completadas e insignias reales con contenido ilustrativo para lo que el backend no expone.
- Base compartida (Hito 0): helper de identidad, `@ModelAttribute("esInvitado")`, excepcion `BackendNoDisponibleException` + patron `errorCarga` con mensaje de reintento, mappers como clases puras con tests.
- Relevar (spike) el contrato de `logistica-service` para `mapaDonaciones`; la vista queda con contenido ilustrativo rotulado hasta que exista una consulta por donante.
- El login/registro funcional y el POST de donacion quedan fuera: este change solo redirige a `/login`; `login.html`, `registro.html` y `autenticacion-service` (cascaron vacio) no se implementan aqui.

### Fuera de alcance (decidido explicitamente)

- Implementar el formulario de login/registro y `autenticacion-service` (`/auth/login`, `/auth/refresh`, `/me`).
- POST de donacion (change propio; ver `plan.md` seccion 5: faltan `subcategoriaId` en el back, form por insumo, `@PostMapping` + PRG).
- Mapa interactivo con libreria de mapas; filtros que filtran de verdad.
- Modificar CSS bajo `static/css/` o el layout base.

## Capabilities

### New Capabilities

- `mis-donaciones`: lista personal de donaciones del donante identificado (GET por donante; sin POST).
- `entidad-beneficiaria`: vidriera publica de entidades (grilla + detalle `{id}`) conectada al backend en modo invitado, con donar -> login.
- `mapa-donaciones`: mapa de donaciones con contenido ilustrativo rotulado y spike del contrato de logistica para el modo personal.
- `incentivos`: vista de misiones e insignias con contenido real (completadas/obtenidas) combinado con ilustrativo, y redirect a login para invitados.

### Modified Capabilities

Ninguna. No hay specs en `openspec/specs/` (estan vacias); el change anterior dejo sus deltas archivados en `openspec/changes/vistas-persona-donante/specs/`.

## Impact

- **Front-end (`front-end-app`)**: `PersonaDonanteController` (handlers pasan de mock a `ApiService` + redirect manual a `/login` + `@ModelAttribute("esInvitado")`), nuevo `IdentidadService` (`donanteIdActual`/`esInvitado`, property `app.donante-demo-id` en `application.yaml`), nueva `BackendNoDisponibleException` + patron `errorCarga`, `EntidadBeneficiariaApiService` (`obtenerTodas`, `obtenerPorId`), `DonacionApiService` (`obtenerPorDonante`), `IncentivosApiService` (`obtenerIncentivos`), mappers puros en `back_services/*/mapper/` con tests en `src/test`, DTOs espejo del backend, templates (cambio de `href` de donar + bloques `errorCarga`, sin tocar CSS).
- **Back-end (`donaciones-service`)**: posible agregado de `direccion`/`descripcion` a `EntidadBeneficiariaResponse` (gap R3 heredado); sin ese cambio la vidriera sale con fallback. Ningun endpoint existente cambia de forma.
- **Servicios relevados**: `incentivos-service` (`MisionResponse`, `InsigniaResponse`, `estadoGeneral` stub) — conectado con merge; `logistica-service` (`rutas`, `entregas`) — solo spike, sin consulta por donante.
- **Auth diferido**: `SecurityConfig` sigue en `permitAll` (el `authenticationEntryPoint` nunca se dispara: redirect manual); `CustomAuthProvider` (devuelve `null`) y `WebApiCallerService` (lee `session.accessToken` que nadie escribe; los servicios del back no validan Bearer) no se tocan. Cada `ApiService` encapsula su llamada en un helper privado para que el swap a `WebApiCallerService` sea 1 linea cuando exista auth.

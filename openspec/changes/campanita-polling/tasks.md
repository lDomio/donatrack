## 1. Lectura backend (notificacion-service)

- [x] 1.1 `Destinatario` suma `usuarioId` + `tipoRol` (enum local `DONANTE`, `ENTIDAD_BENEFICIARIA`) con índice, y `NotificacionRequestDto` suma ambos opcionales que `POST /enviar` persiste; verificar compilación y que los envíos Rabbit existentes siguen guardando con identidad nula
- [x] 1.2 Query en `NotificacionRepository` por `(usuarioId, tipoRol)` ordenada descendente y mapeo a vista (mensaje, fecha del primer estado o null, leída = existe estado `LEIDA`, tipo = `nombrePlantilla`); verificar con test de repositorio
- [x] 1.3 `GET /notificaciones/historial?usuarioId=&tipoRol=` (últimas 20) y `GET /notificaciones/conteo-no-leidas?usuarioId=&tipoRol=`; verificar con curl contra servicio local
- [x] 1.4 `PATCH /notificaciones/{id}/leida` (verifica pertenencia a `usuarioId`/`tipoRol`) y `POST /notificaciones/marcar-todas-leidas`; verificar con curl que el conteo baja a 0

## 2. Identidad mock + proxy SSR (front-end-app)

- [x] 2.1 `IdentidadService` resuelve mock `(usuarioId, tipoRol)` por prefijo de ruta (`/personaDonante/*` → `DONANTE` + `app.donante-demo-id`, `/entidadBeneficiaria/*` → `ENTIDAD_BENEFICIARIA` + nueva `app.entidad-demo-id` default 19, resto/invitado → vacío); verificar con test unitario puro
- [x] 2.2 `NotificacionesApiService` con historial/conteo/marcar-leída (patrón `WebClient` + `BackendNoDisponibleException` + timeout corto como `DonacionApiService`) y constructor de URLs testeable; verificar con test unitario puro
- [x] 2.3 Proxy same-origin `GET /api/notificaciones` (historial + conteo) y `POST /api/notificaciones/{id}/leida` + `POST /api/notificaciones/marcar-todas-leidas` que inyectan identidad server-side sin exponerla al browser; verificar que sin identidad devuelven 204/vacío y con backend caído 503 sin romper
- [x] 2.4 `@ControllerAdvice` inyecta `campanita{mostrar, conteo}` en el modelo de las vistas con header con fallback a vacío; verificar badge SSR con N y oculto en 0 sin 500

## 3. Fragment reutilizable + polling JS

- [x] 3.1 `fragments/campanita.html` único (`th:fragment` con conteo/mostrar) incluido en `fragments/header.html`, más `scriptsExtra` mínimo con `campanita.js` en los templates de entidad que hoy dan 500 por falta del fragment; verificar render con campanita en vistas donante y entidad
- [x] 3.2 `campanita.js`: polling 30s al proxy + refresh al abrir el dropdown, pausa en pestaña oculta, sin fetches solapados, conserva último estado ante 503; verificar badge/dropdown se actualizan sin reload
- [x] 3.3 Marcar una/todas como leídas desde el dropdown y verificar que el badge baja a 0

## 4. Verificación punta a punta

- [x] 4.1 Playwright E2E: crear notificación vía `POST /enviar` con identidad del donante demo, badge en donante, dropdown con contenido, marcar leídas, entidad no ve las del donante, backend caído conserva estado

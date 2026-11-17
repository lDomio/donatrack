## Why

Las personas donantes y entidades beneficiarias no tienen forma de ver en la app las notificaciones de asignación, misión cumplida, suba de categoría y recepción (req 8 y 13 de la consigna). El servicio de notificaciones solo envía por Email/SMS/WhatsApp y no expone lectura, y el header no tiene campanita.

## What Changes

- Agregar lectura de notificaciones in-app con polling (sin websocket en este scope).
- Campanita en el header con badge de no leídas y dropdown de últimas notificaciones.
- Badge con conteo renderizado por SSR en la carga inicial y refrescado por polling cada 30-60s.
- Acción de marcar como leída desde el dropdown.
- Reservar websocket/SSE solo para mapa de camiones en vivo (fuera de este change).

## Capabilities

### New Capabilities
- `notificaciones-campanita`: lectura in-app de notificaciones (historial por destinatario, conteo de no leídas, marcar como leída) consumida por el front con polling.

### Modified Capabilities
(none)

## Impact

- `notificacion-service`: nuevo(s) endpoint(s) GET de lectura (por destinatario + conteo), query en `NotificacionRepository`, modelo `Notificacion/Destinatario` (campo leída si falta).
- `front-end-app`: `NotificacionesApiService` (métodos de lectura), fragment campanita en `fragments/header.html` + CSS existente, JS de polling por página (`scriptsExtra`), posible endpoint proxy `/api/notificaciones` en el front.
- Sin cambios en envío (POST/Rabbit existentes) ni en mapa de camiones.

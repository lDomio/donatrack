## Purpose

Permite a donantes y entidades ver sus notificaciones dentro de la app mediante una campanita con polling, cubriendo los req 8 y 13 sin introducir websocket en este scope.

## ADDED Requirements

### Requirement: Ver conteo de no leídas en la campanita
El sistema SHALL mostrar en el header una campanita con el conteo de notificaciones no leídas del usuario autenticado, renderizado por SSR en la carga inicial.

#### Scenario: Carga inicial con no leídas
- **WHEN** el usuario autenticado carga cualquier página con header
- **THEN** el sistema muestra el badge con el conteo actual de no leídas (0 oculta el badge)

#### Scenario: Usuario invitado sin campanita con datos
- **WHEN** un visitante no autenticado carga la landing pública
- **THEN** el sistema no muestra conteo personal ni dropdown con datos

### Requirement: Listar últimas notificaciones por polling
El sistema SHALL exponer las últimas notificaciones del destinatario (mensaje, fecha, tipo, estado leída) y refrescarlas por polling cada 30-60s sin recargar la página.

#### Scenario: Polling refresca dropdown
- **WHEN** pasan 30-60s o el usuario abre el dropdown
- **THEN** el sistema actualiza la lista y el badge con lo devuelto por el backend

#### Scenario: Backend no disponible
- **WHEN** el servicio de notificaciones no responde
- **THEN** el sistema conserva el último estado renderizado y no rompe la página

### Requirement: Marcar notificaciones como leídas
El sistema SHALL permitir marcar una o todas las notificaciones como leídas y reflejar el nuevo conteo en el badge.

#### Scenario: Marcar todas como leídas
- **WHEN** el usuario acciona "marcar todas como leídas"
- **THEN** el sistema persiste el estado y el badge baja a 0 / se oculta

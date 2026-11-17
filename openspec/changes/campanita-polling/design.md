## Context

Ver proposal.md para el porqué. Estado actual: `notificacion-service` solo tiene POST (`/notificaciones/enviar`, `/inactividad`, `/asignacion-*`) y persiste `Notificacion` con `Destinatario` + `EstadoNotificacion`, sin endpoint GET ni query por destinatario. El front (`front-end-app`, Thymeleaf SSR + JS vanilla, `scriptsExtra` por página) no tiene campanita en `fragments/header.html` (solo CSS huérfano) y `NotificacionesApiService.java` es un stub sin métodos. La decisión de explore fue: polling ahora, websocket reservado para mapa de camiones.

## Goals / Non-Goals

**Goals:**
- Lectura in-app por destinatario con latencia de 30-60s aceptable para req 8/13.
- Reutilizar patrón existente: controller SSR + `WebClient` + fragment Thymeleaf + isla JS.
- Degradación elegante si el backend cae.

**Non-Goals:**
- Websocket/SSE/STOMP (reservado para mapa en vivo, fuera de este change).
- Push en tiempo real <5s, notificaciones push del browser, sonido.
- Cambios en el envío (Email/SMS/WhatsApp/Rabbit existentes).
- Nuevo sistema de preferencias de notificación.
- Campanita para ADMINISTRADOR y CAMIONERO (gate por rol: solo DONANTE y ENTIDAD_BENEFICIARIA).

## Decisions

- **Polling con `setInterval` 30-60s + refresh al abrir el dropdown (vs websocket):** porque los eventos son discretos y pocos por día; evita broker, handshake, puente Rabbit->socket y doble template. Alternativa descartada: WS/STOMP ahora por overkill operativo.
- **SSR pinta badge inicial + JS solo refresca (vs SPA 100% JS):** porque entrega 5 exige motor de plantillas y el auth actual es cookie+sesión server-side; full JS obligaría a rewrite + tokens + CORS.
- **Nuevos GET en `notificacion-service` por destinatario (`/notificaciones/historial` + `/conteo-no-leidas`) y `PATCH /{id}/leida` + `POST /marcar-todas-leidas`:** porque hoy no hay lectura; el front necesita historial + conteo + mutación. La identidad viaja solo server-to-server (ver proxy abajo).
- **Proxy same-origin `/api/notificaciones` en el front + identidad en sesión (vs browser→backend directo):** el JS nunca envía identidad (sería suplantable por query param); el `CampanitaAdvice` la resuelve al renderizar (mock por prefijo de ruta en fase 1, sesión del login en fase 2) y el proxy la lee de la sesión. Sin sesión → 204; backend caído → 503 y el JS conserva estado.
- **Fragment único `fragments/campanita.html` en el header compartido + gate por `TipoRol` (vs por permiso o por copia por rol):** el header ya es común a todos los layouts, así donante y entidad la heredan; el `IdentidadService` decide `(usuarioId, tipoRol)` y el resto se oculta solo.
- **CSRF ignoring en `/api/**` (fase 1):** el JS hace POST con cookie sin token; fase 2 con auth real debe re-exigirlo y exponer el token al JS.
- **Modelo: `leida` + `fecha` consultable por destinatario:** `NotificacionRepository` necesita query por destinatario ordenada por fecha; si `Destinatario` no tiene id estable, definir el identificador (a consensuar con donaciones-service).
- **Reutilizar CSS existente (`.notificacionContenedor`, etc.):** el estilo ya existe, solo falta el fragment HTML + wiring.

## Risks / Trade-offs

- [Latencia 30-60s percibida como "no llega"] → Mitigación: refresh inmediato al abrir dropdown + timestamp "actualizado hace X".
- [Identificador de destinatario ambiguo entre servicios] → Mitigación: acordar `destinatarioId` (email vs id donante/entidad) antes de codificar; queda como primera task.
- [Polling innecesario con pestaña oculta] → Mitigación: pausar con `visibilitychange` y no solapar fetches (`AbortController` o flag).
- [`base.html` fragment `scriptsExtra` frágil (causó 500s)] → Mitigación: la isla JS de campanita debe seguir el patrón que sí funciona en `personaDonante/*` (definir `scriptsExtra` siempre).

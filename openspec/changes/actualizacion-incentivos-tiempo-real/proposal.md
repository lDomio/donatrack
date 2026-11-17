## Why

La vista de Incentivos ya muestra categoría, misión actual, progreso e insignias reales, pero no se actualiza cuando el backend cambia por una acción de la persona administradora o de logística. El resultado esperado del flujo pide actualización visual inmediata de progreso, insignias y categorías. Esa actualización pertenece a Incentivos, no a Mi impacto, y se resuelve con un puente RabbitMQ → WebSocket en lugar de polling.

## What Changes

- Publicar un evento dedicado `IncentivoActualizadoEvent` desde `incentivos-service` cuando cambia el progreso, la misión, la insignia o la categoría de un donante.
- Enviar el evento por RabbitMQ a una cola nueva consumida por `front-end-app`, sin depender de que el donante tenga medios de contacto y sin tocar la cola de `notificacion-service`.
- Agregar un listener en `front-end-app` que reciba el evento y lo envíe por WebSocket a la sesión del donante usando `idUsuarioAuth` como destinatario.
- Configurar un endpoint WebSocket `/ws-incentivos` con destinations por usuario y un handshake que toma `idUsuarioAuth` de la sesión; el `/ws-rutas` del mapa queda intacto.
- Actualizar la vista de Incentivos: al recibir el evento, refrescar el fragmento SSR de la página (categoría, misión, progreso e insignias) sin recargar y sin polling.
- Agregar reconexión, indicador de “actualización en vivo desconectada” y botón de actualización manual como fallback.
- Agregar la configuración de RabbitMQ al `front-end-app` y al `docker-compose.yml`.
- **Non-goals**: no se toca Mi impacto, no se implementa publicación social/N8N, no se modifica el ranking, no se cambia el seed de categorías/misiones y no se reemplaza el sistema de notificaciones existente.

## Capabilities

### New Capabilities

- `actualizacion-incentivos-tiempo-real`: actualización de la vista de Incentivos ante cambios de progreso, insignias o categoría, mediante un evento RabbitMQ entregado por WebSocket a la sesión del donante.

### Modified Capabilities

- Ninguna: `openspec/specs/` no tiene capabilities existentes.

## Impact

- `incentivos-service`: evento `IncentivoActualizadoEvent`, publisher, integración con RabbitMQ y llamadas después del commit en los flujos de donación/reinicio de misiones.
- `front-end-app`: listener RabbitMQ, configuración WebSocket (`/ws-incentivos`, user destinations, handshake), endpoint de fragmento de Incentivos, `incentivos.js`, `incentivos.html`, `application.yaml`.
- `docker-compose.yml`: variables `SPRING_RABBITMQ_*` para el frontend.
- `common-lib`: contrato del evento si se comparte entre servicios.
- Sin cambios de esquema de base de datos; `notificacion-service` no se modifica.

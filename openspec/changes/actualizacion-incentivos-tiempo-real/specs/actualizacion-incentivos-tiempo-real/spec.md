## Purpose

Define la actualización en vivo de la vista de Incentivos cuando cambia el progreso, la misión, las insignias o la categoría de una persona donante, mediante un evento publicado por `incentivos-service` y entregado por WebSocket a la sesión correcta, sin polling.

## ADDED Requirements

### Requirement: El cambio de incentivos genera un evento de actualización

Cuando una donación modifica el progreso, la misión, la insignia o la categoría de una persona donante, `incentivos-service` SHALL publicar un evento de actualización asociado a la identidad de autenticación de esa persona, después de persistir el cambio.

#### Scenario: Progreso de misión actualizado
- **WHEN** una donación registrada o asignada modifica el progreso de la misión actual de la persona donante
- **THEN** el sistema publica un evento de actualización para esa persona

#### Scenario: Insignia o categoría otorgada
- **WHEN** se completa una misión y se otorga una insignia o cambia la categoría
- **THEN** el sistema publica un evento de actualización para esa persona

#### Scenario: Donante sin medios de contacto
- **WHEN** la persona donante no tiene email, SMS ni WhatsApp configurados
- **THEN** el evento de actualización se publica igual, porque la actualización de la vista no depende de los medios de contacto

#### Scenario: Cambio revertido
- **WHEN** la operación que modifica el incentivo falla y la transacción se revierte
- **THEN** no se publica un evento de actualización

### Requirement: El evento se entrega solo a la sesión del donante correspondiente

El sistema SHALL entregar el evento por WebSocket únicamente a las sesiones autenticadas de la persona donante destinataria, identificada por su identidad de autenticación.

#### Scenario: Sesión del donante destinatario
- **WHEN** la persona donante tiene la vista de Incentivos abierta y se publica su evento
- **THEN** su sesión recibe la actualización

#### Scenario: Otra persona donante
- **WHEN** se publica un evento para una persona donante
- **THEN** las sesiones de otras personas donantes no reciben ese evento

#### Scenario: Sesión no autenticada
- **WHEN** una conexión WebSocket no tiene una sesión autenticada de donante
- **THEN** el sistema no le entrega eventos de incentivos

### Requirement: La vista de Incentivos se actualiza sin recargar ni pollear

Al recibir el evento, la vista de Incentivos SHALL actualizar la categoría, la misión actual, el progreso y las insignias sin recargar toda la página y SHALL NOT usar polling para detectar cambios.

#### Scenario: Actualización visual
- **WHEN** llega el evento mientras la persona donante está en Incentivos
- **THEN** la categoría, la misión, el progreso y las insignias se actualizan en la página sin recargarla

#### Scenario: Eventos consecutivos
- **WHEN** llegan varios eventos en pocos segundos
- **THEN** la vista agrupa la actualización y no dispara una recarga por cada evento

#### Scenario: Sin polling
- **WHEN** la persona donante permanece en Incentivos sin recibir eventos
- **THEN** la página no consulta periódicamente el estado de incentivos

### Requirement: La actualización degrada sin romper la vista

Si el WebSocket se desconecta o el evento no puede entregarse, la vista de Incentivos SHALL seguir siendo usable, SHALL indicar que la actualización en vivo no está disponible y SHALL ofrecer una actualización manual.

#### Scenario: WebSocket desconectado
- **WHEN** se pierde la conexión WebSocket
- **THEN** la página conserva el contenido actual y muestra un aviso de actualización en vivo desconectada

#### Scenario: Actualización manual
- **WHEN** la persona donante presiona “Actualizar” con el WebSocket desconectado
- **THEN** la vista consulta el estado actual y lo muestra sin recargar toda la página

#### Scenario: Reconexión
- **WHEN** el WebSocket vuelve a conectarse
- **THEN** la vista retoma la actualización automática y oculta el aviso de desconexión

### Requirement: La actualización en vivo no altera otros flujos

La actualización de Incentivos SHALL NOT modificar el sistema de notificaciones existente, la publicación social, el ranking mensual ni la vista Mi impacto.

#### Scenario: Notificaciones existentes
- **WHEN** se publica un evento de actualización de incentivos
- **THEN** las notificaciones por email, SMS, WhatsApp y campanita siguen funcionando como antes

#### Scenario: Mi impacto
- **WHEN** cambia el progreso o la categoría de la persona donante
- **THEN** la vista Mi impacto no se actualiza en vivo y mantiene su comportamiento de foto

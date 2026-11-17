## 1. Evento y publicación en incentivos-service

- [ ] 1.1 Crear `IncentivoActualizadoEvent` con `idUsuarioAuth`, `idDonanteDonacionesService`, `motivo` y `fecha`; verificar serialización JSON.
- [ ] 1.2 Crear `IncentivoActualizadoPublisher` que publique a `eventos` con routing key `incentivos.actualizado` y omita donantes sin `idUsuarioAuth`; verificar con test unitario.
- [ ] 1.3 Publicar después del commit en `DonacionServiceImpl.agregarDonacion`, `DonacionServiceImpl.marcarDonacionComoRecibida` e `IncentivoServiceImpl.reiniciarMisionesMensuales`; verificar que una transacción revertida no publique.

## 2. Consumo RabbitMQ en front-end-app

- [ ] 2.1 Agregar configuración `spring.rabbitmq` en `front-end-app/src/main/resources/application.yaml` y las variables `SPRING_RABBITMQ_*` al servicio `front-end-app` del `docker-compose.yml`; verificar conexión al broker.
- [ ] 2.2 Agregar `@RabbitListener` con `@QueueBinding` a `incentivos.actualizacion.queue` / exchange `eventos` / routing key `incentivos.actualizado`; verificar que la cola se declare y reciba eventos.
- [ ] 2.3 Implementar el listener para enviar `convertAndSendToUser(String.valueOf(event.idUsuarioAuth()), "/queue/incentivos", event)`; verificar que no toque la cola de `notificacion-service`.

## 3. WebSocket por usuario

- [ ] 3.1 Actualizar `WebSocketConfig` con `enableSimpleBroker("/topic", "/queue")` y `setUserDestinationPrefix("/user")`; verificar que `/topic/posicion-camion` siga funcionando.
- [ ] 3.2 Agregar endpoint `/ws-incentivos` con SockJS y un handshake handler que lea `idUsuarioAuth` de `HttpSession` y lo use como `Principal`; verificar que rechace handshakes sin sesión.
- [ ] 3.3 Verificar que dos sesiones de donantes distintos solo reciban su propio evento y que una sesión anónima no reciba ninguno.

## 4. Actualización de la vista de Incentivos

- [ ] 4.1 Extraer el contenido de `incentivos.html` a un fragmento Thymeleaf reutilizable y agregar `GET /personaDonante/incentivos/contenido` con `@PreAuthorize("hasAuthority('VER_MIS_INCENTIVOS')")`; verificar que devuelva el fragmento con datos reales.
- [ ] 4.2 Agregar SockJS/STOMP a la vista de Incentivos y `incentivos.js` para suscribirse a `/user/queue/incentivos`, refrescar el fragmento con debounce de 300 ms y reemplazar el contenido sin recargar; verificar re-inicialización de filtros.
- [ ] 4.3 Agregar reconexión con backoff, indicador “actualización en vivo desconectada” y botón “Actualizar” que ejecute el mismo refresh; verificar que no haya polling.
- [ ] 4.4 Verificar que el evento no dependa de contactos: un donante sin email/SMS/WhatsApp igual recibe la actualización.

## 5. Verificación

- [ ] 5.1 Ejecutar `mvn -pl incentivos-service -am test` y `mvn -pl front-end-app -am test`; confirmar compilación y tests en verde.
- [ ] 5.2 E2E con navegador: donante con Incentivos abierto; admin registra/asigna una donación; la categoría, misión, progreso e insignias se actualizan sin recargar; verificar UI, request HTTP y estado en la base.
- [ ] 5.3 E2E negativo: otra sesión de donante no recibe el evento; WebSocket desconectado mantiene la página usable y el botón Actualizar refresca.
- [ ] 5.4 Verificar que `notificacion-service` siga enviando/guardando notificaciones y que Mi impacto no tenga polling ni WebSocket.

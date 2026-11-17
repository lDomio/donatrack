## 1. Fix incentivos (NPE + seed duplicado)

- [x] 1.1 Aplicar null-guard en `Donante.java:99` (`categoria` null si no hay misión, `medioContacto` null -> `[]`) y verificar que el módulo compila con `mvn -pl incentivos-service -am -DskipTests package`
- [x] 1.2 Agregar early-return idempotente en `incentivos DataInitializer.java:42` (`count()>0`) y verificar que un segundo arranque con volumen persistente no duplica misiones

## 2. Reintento a nivel compose

- [x] 2.1 Agregar `restart: "on-failure:5"` a `donaciones-service` en `docker-compose.yml` y verificar `docker compose config` es válido

## 3. Verificación E2E (boot limpio)

- [x] 3.1 Ejecutar `docker compose down -v && docker compose up --build` y verificar que `incentivos` loguea seed una vez y `donaciones` loguea `Data Seeding completed` (validado en vivo tras rebuild; `mongo`/`logistica` excluidos por conflicto de puerto 27017 con mongod del host)
- [x] 3.2 Verificar que `docker logs donatrack-incentivos-service | grep "Error en agregarDonante"` está vacío y que `docker logs donatrack-donaciones-service | grep "enviado con éxito idDonante"` muestra éxitos (0 errores, 12+ syncs OK)
- [x] 3.3 Probar `POST localhost:8081/donante {"id":99,"contactos":[]}` dos veces y verificar ambas devuelven `200` (segunda por early-return)

## 4. Alcance agregado durante implementación (requerido para levantar)

- [x] 4.1 IDs asignados en incentivos: quitar `@GeneratedValue(IDENTITY)` de `Donante.id` y `Donacion.id` (con IDENTITY+ID asignado fallan tanto `merge` sobre fila ausente como `persist` por detached); seed con IDs explícitos 1,2,3 / 1-4; `guardarDonante` y update de donación vía `merge()`, creación vía `save()`/rama existe-merge
- [x] 4.2 Resolver `ConflictingBeanDefinitionException rabbitMQConfig` (refactor notificador de `common-lib`): nombre explícito en `@Configuration` de donaciones y notificación
- [x] 4.3 Apuntar `NotificarUsuarioEventPublisher` a `routing-keys.notificacion.general` (la clave `notificaciones` quedó comentada en el yml tras el refactor)
- [x] 4.4 Seed de donaciones sin IDs hardcodeados (`1L/2L/3L`): capturar IDs de `agregarDonante`/`crear` y reutilizarlos (los rollbacks queman AUTO_INCREMENT y los fijos dejan de existir)
- [x] 4.5 Test roto por el fix: `DonacionServiceImplTest` usa `@InjectMocks` sin `EntityManager` → inyección por constructor en ambos services + `@Mock EntityManager` con `merge` stubbed en el test (25/25 incentivos y 21/21 donaciones en verde)

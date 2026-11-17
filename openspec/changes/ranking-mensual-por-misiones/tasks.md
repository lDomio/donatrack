## 1. Backend: scheduler y servicio de ranking

- [x] 1.1 Convertir `GenerarRankingSchedule` en `@Component` y reemplazar su cron por `0 0 3 1 * ?` con `zone = "America/Argentina/Buenos_Aires"`, calculando `YearMonth.now(zone).minusMonths(1)`; verificar que el contexto de Spring registre el bean y que el cron quede con 6 campos válidos.
- [x] 1.2 Corregir `ReinicioDeMisionesSchedule` con el mismo cron y zona; verificar por reflexión o test de contexto que la anotación `@Scheduled` tenga la expresión y zona esperadas.
- [x] 1.3 Agregar `RankingService.generarRanking(YearMonth periodo)` y su implementación transaccional: eliminar los rankings existentes del período y guardar uno nuevo con `fechaRanking = periodo.atDay(1)`; verificar con un test que dos generaciones del mismo período dejan una sola fila.
- [x] 1.4 Cambiar `buscarPosicionDonanteEnRanking(Long id)` para leer el último ranking persistido en vez de generar uno nuevo; verificar que llamar a la consulta no incremente la cantidad de rankings.
- [x] 1.5 Cambiar el orden de `RankingDonantes.generarRanking(List<Donante>)`: misiones del período descendente, donaciones del período descendente, nombre ascendente; filtrar donantes con cero misiones en el período y asignar puestos consecutivos; verificar con tests de orden, empate y exclusión.

## 2. Backend: endpoint de generación a demanda

- [x] 2.1 Crear `GenerarRankingRequest(Integer mes, Integer anio)` con validación de mes 1-12 y año >= 2000; verificar que un request inválido devuelva 400.
- [x] 2.2 Agregar `POST /ranking/generar` en `RankingController` con `@PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('VER_RANKING')")`, que llame a `generarRanking(periodo)` y devuelva `RankingResponse`; verificar con test que un admin recibe 200 y un usuario sin permiso 403.
- [x] 2.3 Verificar que `RankingResponse` exponga `misionesCompletadas` y `cantidadDonaciones` del período para que la UI pueda mostrar ambas columnas.

## 3. Frontend: generación y visualización

- [x] 3.1 Agregar `GenerarRankingRequestDto` y `IncentivosApiService.generarRanking(Integer mes, Integer anio)` que haga `POST /ranking/generar` con JWT; verificar la URL y el body con un test.
- [x] 3.2 Agregar `POST /personaAdministradora/ranking/generar` en `PersonaAdministradoraController` con `@PreAuthorize("hasAuthority('VER_RANKING')")`: parsear `periodo` (`YYYY-MM`), llamar al servicio, agregar flash `exito`/`error` y redirigir a `/personaAdministradora/ranking?periodo=...`; verificar el redirect y los flash attributes.
- [x] 3.3 En `ranking.html`, agregar el formulario POST con el `periodo` seleccionado y el botón "Generar ranking"; sincronizar el hidden con el select de filtro y verificar que el POST incluya el período correcto y el token CSRF.
- [x] 3.4 En `ranking.html`, agregar la columna "Misiones" (principal) y conservar "Donaciones" como dato secundario; actualizar subtítulo y caption para indicar que el orden es por misiones del período.
- [x] 3.5 Agregar el bloque de alertas `alertaFormulario alertaExito/alertaError` en `ranking.html` y reutilizar los estilos existentes; verificar que el mensaje se vea después del redirect.
- [x] 3.6 Verificar que el estado vacío de un período sin ranking ofrezca el botón "Generar ranking" y no muestre una tabla vacía.

## 4. Seed y backfill

- [x] 4.1 Reemplazar en `DataInitializer` el armado manual del ranking inicial por `new RankingDonantes(periodo.atDay(1), donanteRepository.findAll())`, usando el mes anterior; verificar que el ranking inicial quede ordenado por misiones y no por orden de inserción.
- [x] 4.2 Documentar y ejecutar el backfill del ranking de octubre 2026 con `POST /ranking/generar` después del deploy; verificar que el ranking persistido tenga `fechaRanking=2026-10-01` y el orden correcto.

## 5. Tests automatizados

- [x] 5.1 Agregar tests de `RankingDonantes`: orden por misiones, desempate por donaciones, desempate por nombre, exclusión de donantes con cero misiones y asignación de puestos.
- [x] 5.2 Agregar tests de `RankingServiceImpl`: generación para un período, reemplazo idempotente, `fechaRanking` en el primer día y `buscarPosicionDonanteEnRanking` sin efecto de generación.
- [x] 5.3 Agregar tests de `RankingController`: POST exitoso, request inválido, usuario sin permiso y respuesta con misiones/donaciones.
- [x] 5.4 Agregar tests de frontend para `IncentivosApiService.generarRanking`, `PersonaAdministradoraController` y el render de la columna "Misiones"/botón de generación.
- [x] 5.5 Ejecutar `mvn -pl incentivos-service -am test` y `mvn -pl front-end-app -am test`; confirmar BUILD SUCCESS y todos los tests en verde.

## 6. Verificación E2E

- [x] 6.1 Levantar el stack, entrar como administrador y verificar que el ranking de octubre 2026 muestre posición, donante, misiones y donaciones, con el podio destacado.
- [x] 6.2 Presionar "Generar ranking" para octubre 2026 y verificar que el ranking se recalcule, que el mensaje de éxito aparezca y que el orden sea por misiones.
- [x] 6.3 Consultar la DB `incentivos_service.ranking_de_donantes` y `donante_en_ranking`: un solo ranking para el período, sin duplicados, con `fecha_ranking` en el primer día del mes.
- [x] 6.4 Verificar que el job programado quede registrado como bean y que su cron/zona sean `0 0 3 1 * ?` / `America/Argentina/Buenos_Aires` (test de contexto o logs de arranque).
- [x] 6.5 Verificar que consultar `/incentivos/{id}/metricas` no cree un ranking nuevo y que la vista del administrador siga mostrando el último ranking persistido.

## 1. Backend: autorización de métricas

- [ ] 1.1 Agregar `@PreAuthorize("hasRole('DONANTE') and hasAuthority('VER_MIS_DONACIONES')")` y validación de pertenencia con `donanteService.validarUsuarioDeDonante(...)` a `GET /incentivos/{id}/metricas`; verificar con test de donante propio y ajeno.
- [ ] 1.2 Agregar tests del controller para acceso propio, acceso con otro `id` y acceso sin permiso.

## 2. Frontend: datos y mapeo

- [ ] 2.1 Crear `MetricasResponse` espejo de `MetricasUsuarioResponse` con `@JsonIgnoreProperties(ignoreUnknown = true)`; verificar deserialización de `donacionesPorPeriodo` y `comparacionDeMeses`.
- [ ] 2.2 Agregar `IncentivosApiService.obtenerMetricas(donanteId, mesInicio, mesFin)` con construcción de URL y test unitario de query params.
- [ ] 2.3 Crear `ImpactoMapper`/`ImpactoVistaDto` que ordene los períodos, tome los últimos 12 meses y resuelva el label de ranking (“Último ranking”/“Sin ranking”); verificar con tests de mapa vacío, un mes y más de 12.

## 3. Frontend: vista Mi impacto

- [ ] 3.1 Agregar `GET /personaDonante/impacto` en `PersonaDonanteController` con `@PreAuthorize("hasAuthority('VER_MIS_DONACIONES')")`, resolución del donante desde sesión y carga de métricas; verificar respuesta 200 y redirect a login sin sesión.
- [ ] 3.2 Crear `templates/personaDonante/impacto.html` con KPIs, gráfico, estados vacío/error, enlace a Incentivos y botón Actualizar; verificar render con datos y sin datos.
- [ ] 3.3 Crear `static/css/personaDonante/impacto.css` para el dashboard y el gráfico de barras; verificar responsive y accesibilidad básica.
- [ ] 3.4 Crear `static/js/personaDonante/impacto.js` para dibujar el gráfico desde los datos del template y manejar Actualizar; verificar que no haya `setInterval`, WebSocket ni SSE.
- [ ] 3.5 Agregar “Mi impacto” en `layout/basePersonaDonante.html` (nav desktop, dropdown y mobile) con `vistaActual == 'impacto'`; verificar que el enlace activo se marque.

## 4. Verificación

- [ ] 4.1 Ejecutar `mvn -pl incentivos-service -am test` y `mvn -pl front-end-app -am test`; confirmar compilación y tests en verde.
- [ ] 4.2 E2E con navegador: donante con donaciones abre Mi impacto, ve total/organizaciones/ranking/gráfico y los valores coinciden con `/metricas`; no ve misión, progreso, insignias ni categoría.
- [ ] 4.3 Verificar estados negativos: donante sin donaciones (ceros/estado vacío), sin ranking (“Sin ranking”), backend caído (error sin datos ficticios).
- [ ] 4.4 Verificar que la página no hace polling ni abre WebSocket (sin requests periódicos en la pestaña de red).

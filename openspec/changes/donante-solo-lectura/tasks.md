## 1. Aviso compartido

- [x] 1.1 Agregar `.avisoDonacionPresencial` en `front-end-app/src/main/resources/static/css/personaDonante/compartidoPersonaDonante.css` (fondo celeste suave, borde, ícono y texto en flex) y verificar que la clase exista una sola vez en el archivo.

## 2. Vistas del donante en solo lectura

- [x] 2.1 En `entidadesBeneficiarias.html`, quitar el bloque `bloqueDonacionDirecta` con los dos enlaces "Donar ahora" y verificar que no quede ninguna referencia a `botonDonarAhora` en el template.
- [x] 2.2 En `entidadesBeneficiarias.html`, quitar el `<th:block>` que renderiza `fragments/modalDonacion` y los includes de `modalDonacion.css` y `modalDonacion.js`; verificar con búsqueda que el template no mencione `modalDonacion`.
- [x] 2.3 En `verNecesidadEntidad.html`, quitar `pieCategoriaNecesidad` con los dos enlaces "Donar a esta categoría" y verificar que no quede ninguna referencia a `botonDonarCategoria`.
- [x] 2.4 En `verNecesidadEntidad.html`, quitar el `<th:block>` del modal y los includes de `modalDonacion.css` y `modalDonacion.js`; verificar con búsqueda que el template no mencione `modalDonacion`.
- [x] 2.5 Ajustar los subtítulos a copy de consulta y agregar el aviso `avisoDonacionPresencial` en ambas vistas; verificar visualmente que el aviso aparezca debajo del encabezado y antes del contenido de listado/necesidades.

## 3. Limpieza del flujo de escritura

- [x] 3.1 En `PersonaDonanteController`, eliminar el método `@PostMapping("/donaciones") crearDonacion(...)` y verificar que no quede ningún `@PostMapping` en el controller.
- [x] 3.2 En `PersonaDonanteController`, eliminar imports sin uso (`dto.BienRequest`, `dto.CrearDonacionRequest`, `PostMapping`, `RedirectAttributes`), la constante `UNIDADES_MEDIDA`, los dos `model.addAttribute("unidadesMedida", ...)` y el `@ModelAttribute("esInvitado")`; verificar con búsqueda que no queden referencias a esos símbolos en la clase.
- [x] 3.3 En `DonacionApiService`, eliminar el método `crear(CrearDonacionRequest)` y su import de `dto.CrearDonacionRequest`; verificar que el servicio compile y que `registrarDonacionEnDeposito(...)` siga intacto con los DTOs de `dto.donante`.

## 4. Borrado de huérfanos

- [x] 4.1 Eliminar `front-end-app/src/main/resources/templates/fragments/modalDonacion.html` y verificar que no queden referencias a `fragments/modalDonacion` en el proyecto.
- [x] 4.2 Eliminar `static/css/personaDonante/modalDonacion.css` y `static/js/personaDonante/modalDonacion.js`; verificar que ningún template los incluya.
- [x] 4.3 Eliminar `dto/CrearDonacionRequest.java` y `dto/BienRequest.java`; verificar con búsqueda global que no queden imports ni usos de `ar.edu.utn.frba.dds.front_end_app.dto.CrearDonacionRequest` ni `...dto.BienRequest`.
- [x] 4.4 Quitar de `entidadesBeneficiarias.css` los selectores `.bloqueDonacionDirecta`, `.botonDonarAhora` y `.botonDonarAhora:hover`, y de `verNecesidadEntidad.css` los selectores `.pieCategoriaNecesidad`, `.botonDonarCategoria` y `.botonDonarCategoria:hover`; verificar con búsqueda que no queden en esos archivos.

## 5. Verificación

- [x] 5.1 Ejecutar búsquedas de control y confirmar cero resultados en `front-end-app/src/main` para `modalDonacion`, `Donar ahora`, `Donar a esta categoría`, `unidadesMedida`, `dto.BienRequest` y `dto.CrearDonacionRequest`.
- [x] 5.2 Ejecutar `mvn -pl front-end-app -am test` y confirmar que compila y que `PersonaDonanteEntidadesControllerTest` sigue en verde.
- [x] 5.3 Levantar el frontend y recorrer como donante: listado de entidades, detalle de necesidades y "Mis donaciones"; confirmar que no hay ninguna acción de donar y que el aviso presencial se ve en las dos primeras vistas.

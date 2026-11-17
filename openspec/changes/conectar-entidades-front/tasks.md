## 1. Param busqueda OR (backend)

- [x] 1.1 Agregar `busqueda` opcional a `GET /entidades-beneficiarias` con OR sobre razonSocial/calle/ciudad (ignora `nombre`/`ubicacion` si viene) y verificar con test de servicio: por nombre, por ubicación, combinado con tipo, ausente = todo igual
- [x] 1.2 Exponer `busqueda` en el controller y verificar con MockMvc: forward de params, 200 con resultados, backend existente sin cambios

## 2. Wiring front (browser)

- [x] 2.1 Pasar `busqueda`/`tipo` del form al backend en `PersonaDonanteController` + `EntidadBeneficiariaApiService` (blank = no enviar) preservando valores activos en el form, y verificar con test del ApiService/controller que los params llegan
- [x] 2.2 Mostrar conteo real en la tarjeta para logueados (`DonacionApiService` + DTO espejo + `donanteIdActual`; invitados sin tarjeta; fallo = página sin cifra con reintento) y verificar render con conteo mockeado, invitado e indisponibilidad
- [x] 2.3 Actualizar Postman (`busqueda`) y verificar cada escenario de las specs contra respuestas reales

## 3. Validación integral

- [x] 3.1 Pasar `openspec validate "conectar-entidades-front" --strict` y los tests de `donaciones-service` y `front-end-app` en verde, verificando que el listado sin params y `misDonaciones` siguen intactos

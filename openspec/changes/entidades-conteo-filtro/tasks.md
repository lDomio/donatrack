## 1. Conteo mensual (backend)

- [x] 1.1 Agregar query `COUNT DISTINCT entidad` por donante + rango de mes en `DonacionRepository` y verificar con test de repositorio (2 entidades distintas + 1 sin necesidad + 1 de mes anterior → cuenta 2)
- [x] 1.2 Crear DTO `EntidadesAyudadasResponse(donanteId, anio, mes, cantidad)` con `Clock` inyectado (`America/Argentina/Buenos_Aires`) y verificar que serializa el JSON esperado
- [x] 1.3 Exponer `GET /donaciones/donante/{idDonante}/entidades-ayudadas-este-mes` en `DonacionController`/`DonacionService` (valida donante → 404) y verificar con MockMvc: 200 con cantidad, 404 donante inexistente, 0 sin ayudas

## 2. Filtro de entidades (backend)

- [x] 2.1 Extender `GET /entidades-beneficiarias` con `nombre/ubicacion/tipo` opcionales (AND, ausente/vacío/`Todas` = no filtra) preservando `obtenerTodas()` sin params y verificar con MockMvc sin params = todas
- [x] 2.2 Implementar predicados `nombre` (ILIKE razonSocial) y `ubicacion` (ILIKE calle OR ciudad, null-safe) y verificar con test: cada filtro solo + combinados AND
- [x] 2.3 Implementar tabla de equivalencias de `tipo` (Comedores→COMEDOR, Educación→EDUCACION, Salud→SALUD, Refugios→REFUGIO, Todas→sin filtro; 400 con valores válidos si desconocido) y verificar con test: cada alias, Todas, y 400 ante valor inválido

## 3. Validación integral

- [x] 3.1 Pasar `openspec validate --change entidades-conteo-filtro --strict` y `./mvnw -pl donaciones-service test` (o `mvn -pl donaciones-service test`) en verde y verificar que no rompen endpoints existentes (`GET /entidades-beneficiarias`, `GET /donaciones/donante/{id}`)
- [x] 3.2 Actualizar colección Postman / ejemplos de contrato de ambos endpoints y verificar cada escenario de las specs con respuestas reales

## 1. Implementación del envío de URLs codificadas

- [x] 1.1 En `WebApiCallerService`, agregar overloads `get(URI url, Class<T>)` y `getList(URI url, Class<T>)` que usen `webClient.get().uri(uri)` y verificar que el módulo compile.
- [x] 1.2 En `EntidadBeneficiariaApiService`, cambiar `construirUrl(...)` para que devuelva `java.net.URI` usando `.build().encode().toUri()`, y hacer que `obtenerConFiltros(...)` llame a `getList(URI, ...)`; verificar con búsqueda que ese servicio ya no use `.toUriString()` ni `getList(String, ...)`.

## 2. Tests de regresión

- [x] 2.1 Actualizar `EntidadBeneficiariaApiServiceUrlTest` para comparar el `URI` resultante (o su `toString()`) y verificar que `construirUrl` conserva `tipo=Educación`, `busqueda=Colón` y la combinación de ambos.
- [x] 2.2 Agregar un caso con un carácter especial en `busqueda` (por ejemplo `A&B`) y confirmar que el valor queda escapado una sola vez.

## 3. Verificación

- [x] 3.1 Ejecutar `mvn -pl front-end-app -am test` y confirmar que compila y que los tests de URL pasan.
- [x] 3.2 Levantar frontend y backend, iniciar sesión como donante y verificar que `?tipo=Educación` responde sin el mensaje "No se pudieron cargar las entidades".
- [x] 3.3 Verificar una búsqueda con acentos (por ejemplo `Colón`) y confirmar que el backend recibe el texto original y no la cadena codificada literal.

## Why

En la vista de entidades beneficiarias, filtrar por "Educación" devuelve el mensaje "No se pudieron cargar las entidades" porque el parámetro llega doblemente codificado al backend (`Educaci%25C3%25B3n`) y `donaciones-service` responde 400. El mismo defecto afecta la búsqueda por texto con acentos (por ejemplo "Colón"), que devuelve resultados vacíos sin avisar.

## What Changes

- Corregir la construcción y el envío de query params con acentos en el frontend para que el valor se codifique una sola vez.
- Cubrir con tests de URL el caso `tipo=Educación` y la búsqueda con acentos, verificando que el valor llega al backend como `Educación` y no como `Educaci%C3%B3n`.
- Sin cambios de contrato con `donaciones-service`, sin cambios de datos ni de seed.
- **Non-goals**: no se agrega estado vacío para filtros sin resultados, no se siembran entidades de tipo `EDUCACION`, y no se modifican los demás filtros ni el backend.

## Capabilities

### New Capabilities

- `filtros-entidades-beneficiarias`: la persona donante puede filtrar entidades por tipo y buscar por texto, incluyendo valores con acentos, sin errores de carga ni resultados silenciosamente vacíos por encoding.

### Modified Capabilities

- Ninguna: `openspec/specs/` no tiene capabilities existentes.

## Impact

- Frontend: `EntidadBeneficiariaApiService.construirUrl`, `WebApiCallerService` (envío de URLs ya codificadas) y tests de URL del servicio.
- Sin cambios en `donaciones-service`, en la base de datos ni en los templates.
- El fix aplica también a cualquier búsqueda con acentos en el campo `busqueda`.

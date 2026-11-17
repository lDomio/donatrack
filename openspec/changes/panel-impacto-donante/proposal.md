## Why

La persona donante puede ver el detalle de sus donaciones y el contador mensual de entidades ayudadas, pero no tiene una vista consolidada de su impacto histórico. El endpoint `GET /incentivos/{id}/metricas` ya devuelve total histórico, organizaciones ayudadas, donaciones por período y posición en el ranking, pero el frontend no lo consume. Además, el progreso, las insignias y la categoría deben seguir viviendo en Incentivos, no duplicarse acá.

## What Changes

- Agregar la vista **Mi impacto** en `/personaDonante/impacto` y su enlace en el menú del donante (desktop, dropdown y mobile).
- Consumir `GET /incentivos/{id}/metricas` desde el frontend mediante un DTO espejo y un método en `IncentivosApiService`.
- Mostrar KPIs de impacto: total histórico de donaciones, organizaciones ayudadas y posición en el último ranking.
- Mostrar un gráfico de barras de donaciones por mes (últimos 12 meses) y, si hay datos, una comparación mes actual vs anterior.
- Incluir un enlace a Incentivos para progreso, insignias y categoría; **no** duplicar esos datos en Mi impacto.
- Agregar autorización y validación de pertenencia al endpoint `/metricas`, que hoy no las tiene.
- La vista es una **foto**: se carga al ingresar y no tiene polling ni WebSocket. Puede ofrecer un botón “Actualizar”.
- **Non-goals**: no se modifica el seed de categorías/misiones; no se implementa publicación social; no se toca la actualización en vivo de Incentivos; no se agregan tablas ni endpoints nuevos en `incentivos-service`.

## Capabilities

### New Capabilities

- `impacto-donante`: panel de solo lectura con las métricas históricas de impacto de la persona donante, sin actualización en tiempo real ni duplicación del progreso de incentivos.

### Modified Capabilities

- Ninguna: `openspec/specs/` no tiene capabilities existentes.

## Impact

- Frontend: `PersonaDonanteController`, `IncentivosApiService`, DTO de métricas, `templates/personaDonante/impacto.html`, `static/css/personaDonante/impacto.css`, `static/js/personaDonante/impacto.js` (solo gráfico/refresh manual), `layout/basePersonaDonante.html`.
- Backend: `IncentivoController` (autorización de `/metricas`), sin cambios de esquema.
- Sin cambios en donaciones-service, logistica-service, notificacion-service ni en la base de datos.

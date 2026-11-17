## Why

El ranking mensual de donantes no se está generando ni ordenando según la consigna: `GenerarRankingSchedule` no es un bean, el cron está mal (`* * * 1 * ?`), el ranking inicial se persiste sin ordenar y la UI muestra donaciones en vez de misiones. El admin tampoco tiene forma de recalcular un período a demanda.

## What Changes

- Convertir `GenerarRankingSchedule` en bean y corregir su cron a las **03:00 del día 1** (`0 0 3 1 * ?`, zone `America/Argentina/Buenos_Aires`), calculando el **mes anterior** (el que acaba de cerrar).
- Corregir también el cron de `ReinicioDeMisionesSchedule` con la misma expresión y zona.
- Ordenar el ranking por **misiones completadas en el período** (descendente), desempatando por donaciones del período (descendente) y luego por nombre (ascendente).
- Persistir **un ranking por período**; regenerar el mismo mes reemplaza el existente en vez de duplicarlo.
- Usar el primer día del período como `fechaRanking`, para que el período del ranking no dependa de la fecha de generación.
- Agregar `POST /ranking/generar` (admin, `VER_RANKING`) que recalcula el período recibido (`mes`, `anio`) y devuelve el ranking generado.
- Agregar en la vista `ranking.html` un botón **“Generar ranking”** para el período seleccionado, con mensaje de éxito/error.
- Mostrar la columna **“Misiones”** en la tabla y actualizar subtítulo/caption para reflejar el criterio de orden.
- Reemplazar el ranking inicial sin ordenar del `DataInitializer` por una generación real del período anterior (o quitarlo si no hay datos).
- **Non-goals**: panel de impacto del donante, publicación social/N8N, progreso real de misiones en la UI y notificaciones.

## Capabilities

### New Capabilities

- `ranking-mensual-donantes`: generación programada y a demanda del ranking mensual de donantes, ordenado por misiones cumplidas en el período y visible para la persona administradora.

### Modified Capabilities

- Ninguna: `openspec/specs/` no tiene capabilities existentes.

## Impact

- `incentivos-service`: `GenerarRankingSchedule`, `ReinicioDeMisionesSchedule`, `RankingService`/`RankingServiceImpl`, `RankingDonantes`, `RankingController`, `RankingResponse`, `DataInitializer`.
- `front-end-app`: `IncentivosApiService`, `PersonaAdministradoraController`, `ranking.html`, `ranking.css` (estilos de alerta/botón si faltan).
- Sin cambios de contrato con otros servicios ni de esquema de base de datos (se reutiliza `ranking_de_donantes`).
- Los rankings existentes se pueden regenerar con el nuevo endpoint; el ranking de octubre 2026 queda como backfill de verificación.

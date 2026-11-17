## Context

Ver `proposal.md` para la motivación y `specs/donante-solo-lectura/spec.md` para el contrato de comportamiento.

Estado actual en `ENTREGA_6`:

- El donante crea donaciones desde dos vistas: el listado de entidades ("Donar ahora") y el detalle de necesidades ("Donar a esta categoría"), ambas abriendo el fragmento compartido `fragments/modalDonacion`.
- El formulario del modal hace `POST /personaDonante/donaciones`; el controller arma un `dto.CrearDonacionRequest` y llama a `DonacionApiService.crear(...)`, que hace `POST /donaciones` en `donaciones-service`.
- El flujo del administrador ya existe completo en `registroDeDonaciones`: busca al donante, arma los bienes y registra la donación con `idDonante` mediante `registrarDonacionEnDeposito(...)`, usando `dto.donante.CrearDonacionRequest` y `dto.donante.BienRequest`.
- Hay dos pares de DTOs con el mismo nombre: `dto.*` (donante) y `dto.donante.*` (administrador). El primer par solo lo usa el flujo que se retira.
- No hay tests que cubran el `POST` del donante.

## Goals / Non-Goals

**Goals:**
- Dejar el frontend del donante sin ninguna acción de alta de donaciones.
- Eliminar la duplicación de DTOs y el código muerto que quedaría tras desconectar el flujo.
- Explicar en la UI por qué ya no se dona desde la aplicación.

**Non-Goals:**
- Cambiar la autorización del backend: `POST /donaciones` sigue aceptando el rol DONANTE.
- Arreglar los filtros de "Mis donaciones", que hoy envían parámetros que el controller ignora.
- Modificar el flujo del administrador o el contrato con `donaciones-service`.

## Decisions

- **Limpieza completa vs dejar código dormido:** se eliminan el fragmento, CSS, JS, DTOs y método de servicio. Alternativa descartada: dejar el modal y los DTOs sin usar; mantiene dos fuentes para el mismo request y facilita que se reconecte el flujo por error.
- **Borrar el endpoint de frontend en vez de protegerlo:** se elimina `POST /personaDonante/donaciones` completo. Alternativa descartada: dejarlo respondiendo 403; conserva una ruta de escritura en el frontend que la consigna no admite.
- **Un solo par de DTOs:** se conserva `dto.donante.*` porque el alta del administrador necesita estado, vencimiento y foto por bien. Se elimina `dto.*`, que era el contrato reducido del donante.
- **Aviso compartido:** el mensaje de registro presencial se define una vez en `compartidoPersonaDonante.css` y se reutiliza en las dos vistas. Alternativa descartada: duplicar estilos por página.
- **Copia neutra en los encabezados:** se reemplazan las frases que invitaban a donar por variantes de consulta ("conocé cómo ayudar", "consultá qué falta"), para no contradecir la ausencia de acciones.
- **Eliminar `@ModelAttribute("esInvitado")`:** tras quitar los bloques de donación ninguna vista del donante lo usa; se verificó con búsqueda global. Alternativa descartada: conservarlo por si se reutiliza; sería estado sin consumidor.

## Risks / Trade-offs

- [Riesgo] El backend sigue permitiendo que un DONANTE cree donaciones con un `POST` directo → Mitigación: se acepta como deuda explícita; un change posterior puede quitar `puedeCrearDonacion` del validador y el branch DONANTE del `@PreAuthorize`.
- [Riesgo] Borrar `dto.CrearDonacionRequest`/`dto.BienRequest` rompa consumidores no detectados → Mitigación: búsqueda global previa en `front-end-app/src/main` y `front-end-app/src/test`; el único consumidor es el controller que se elimina.
- [Riesgo] Quitar el modal deje referencias en CSS/JS de las vistas → Mitigación: se eliminan los includes y se verifica con búsqueda de `modalDonacion`, `botonDonarAhora` y `botonDonarCategoria`.
- [Trade-off] "Mis donaciones" sigue con filtros que no filtran → se deja igual para no mezclar un bug preexistente con este change; queda registrado como deuda.

## Migration Plan

1. Aplicar los cambios de templates y controller; compilar `front-end-app`.
2. Eliminar el método de servicio y los DTOs huérfanos; compilar de nuevo.
3. Ejecutar tests de `front-end-app` y búsquedas de verificación.
4. Rollback: revertir el change completo; no hay migración de datos ni cambios de contrato.

## Why

La consigna del TP establece que la persona donante no dona desde la aplicación: se acerca al depósito y una persona administradora registra la donación a su nombre. En `ENTREGA_6` el frontend del donante todavía permite crear donaciones directamente desde "Entidades beneficiarias" y "Ver necesidad" mediante el modal compartido y el `POST /personaDonante/donaciones`.

## What Changes

- Quitar de `entidadesBeneficiarias.html` el bloque "Donar ahora" y el modal de donación asociado.
- Quitar de `verNecesidadEntidad.html` el bloque "Donar a esta categoría" y el modal de donación asociado.
- Eliminar el endpoint de frontend `POST /personaDonante/donaciones` y el método `DonacionApiService.crear(...)`, únicos consumidores del flujo de donación del donante.
- Eliminar los artefactos huérfanos del flujo: `fragments/modalDonacion.html`, `modalDonacion.css`, `modalDonacion.js`, `dto/CrearDonacionRequest.java` y `dto/BienRequest.java` (el flujo admin usa las variantes de `dto/donante/`).
- Mantener la navegación de solo lectura: listado de entidades, detalle de necesidades y "Mis donaciones" con su historial.
- Agregar un aviso informativo en las vistas del donante indicando que las donaciones se registran presencialmente en el depósito.
- Ajustar el copy que invitaba a donar desde la aplicación ("elegí dónde ayudar", "qué podés donar").
- **Non-goals**: no se modifica la autorización del backend (`POST /donaciones` sigue aceptando el rol DONANTE), no se tocan los filtros de "Mis donaciones" (hoy no filtran), ni el flujo del administrador, que ya registra donaciones en depósito con `idDonante`.

## Capabilities

### New Capabilities

- `donante-solo-lectura`: la persona donante puede consultar entidades, necesidades y sus donaciones, pero el frontend no le ofrece ninguna acción para crear una donación; el alta queda exclusivamente en el flujo del administrador.

### Modified Capabilities

- Ninguna: `openspec/specs/` no tiene capabilities existentes.

## Impact

- Frontend: `PersonaDonanteController`, `DonacionApiService`, templates `entidadesBeneficiarias.html` y `verNecesidadEntidad.html`, `fragments/modalDonacion.html`, CSS/JS del modal y de los bloques de donación.
- DTOs: se eliminan `dto/CrearDonacionRequest.java` y `dto/BienRequest.java`; se conservan `dto/donante/CrearDonacionRequest.java` y `dto/donante/BienRequest.java` para el alta del administrador.
- Sin cambios de contrato con `donaciones-service` ni cambios de backend.
- Este change revierte, a nivel de experiencia del donante, la capacidad introducida por `unificar-form-donacion`; el flujo admin de `registroDeDonaciones` queda como único camino de alta.

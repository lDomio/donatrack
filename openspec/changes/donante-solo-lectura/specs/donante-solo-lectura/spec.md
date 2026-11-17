## Purpose

Define el comportamiento de solo lectura de la experiencia de la persona donante: puede consultar entidades, necesidades y su historial de donaciones, pero no puede registrar donaciones desde la aplicación porque el alta se realiza presencialmente en el depósito a través de una persona administradora.

## ADDED Requirements

### Requirement: La persona donante no puede registrar donaciones desde la aplicación

La aplicación SHALL NOT ofrecer a la persona donante ninguna acción, formulario o flujo para crear una donación. El registro de donaciones queda exclusivamente en el flujo de la persona administradora.

#### Scenario: Listado de entidades sin acción de donar
- **WHEN** la persona donante abre el listado de entidades beneficiarias
- **THEN** ve las entidades y puede navegar a su detalle, pero no ve ningún botón, enlace o modal para donar

#### Scenario: Detalle de necesidades sin acción de donar
- **WHEN** la persona donante abre el detalle de una entidad con sus necesidades
- **THEN** ve el progreso de cada categoría y no ve ningún botón, formulario o modal para donar

#### Scenario: Intento de registrar una donación desde el frontend
- **WHEN** se envía una petición al endpoint de donaciones de la persona donante que existía antes de este change
- **THEN** la aplicación no registra la donación y responde con un error de ruta inexistente

### Requirement: La persona donante conserva la consulta de solo lectura

La aplicación SHALL permitir a la persona donante consultar el listado de entidades, el detalle de necesidades y su historial de donaciones sin ofrecer operaciones de creación, edición o borrado sobre donaciones.

#### Scenario: Consulta del historial de donaciones
- **WHEN** la persona donante abre "Mis donaciones"
- **THEN** ve sus donaciones con estado y datos de seguimiento, sin acciones para modificarlas

#### Scenario: Consulta de necesidades de una entidad
- **WHEN** la persona donante navega desde una entidad a sus necesidades
- **THEN** puede ver qué falta en cada categoría y volver al listado de entidades

### Requirement: Las vistas del donante explican el registro presencial

Las vistas del donante vinculadas a donaciones SHALL mostrar un aviso que indique que las donaciones se registran presencialmente en el depósito por una persona administradora.

#### Scenario: Aviso en el listado de entidades
- **WHEN** la persona donante abre el listado de entidades beneficiarias
- **THEN** ve un aviso indicando que debe acercarse al depósito con los bienes y que una persona administradora los registra a su nombre

#### Scenario: Aviso en el detalle de necesidades
- **WHEN** la persona donante abre el detalle de necesidades de una entidad
- **THEN** ve el mismo aviso sobre el registro presencial en el depósito

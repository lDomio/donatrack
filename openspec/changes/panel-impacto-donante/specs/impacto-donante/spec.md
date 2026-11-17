## Purpose

Define el panel **Mi impacto** de la persona donante: una vista de solo lectura que consolida sus métricas históricas de donaciones, organizaciones ayudadas y posición en el ranking, sin duplicar el progreso, las insignias ni la categoría que pertenecen a Incentivos.

## ADDED Requirements

### Requirement: El panel muestra las métricas históricas de impacto

La vista **Mi impacto** SHALL mostrar el total histórico de donaciones, la cantidad de organizaciones beneficiarias ayudadas y la posición en el último ranking mensual disponible.

#### Scenario: Donante con historial
- **WHEN** la persona donante abre Mi impacto y tiene donaciones registradas
- **THEN** ve el total histórico, las organizaciones ayudadas y su posición en el último ranking

#### Scenario: Donante sin donaciones
- **WHEN** la persona donante abre Mi impacto y no tiene donaciones
- **THEN** ve los indicadores en cero y un estado vacío que explica que todavía no registró donaciones

#### Scenario: Sin ranking disponible
- **WHEN** no existe un ranking mensual persistido para mostrar
- **THEN** la vista indica que no hay ranking disponible en lugar de mostrar una posición inventada

### Requirement: El panel muestra la evolución histórica de donaciones

La vista **Mi impacto** SHALL mostrar un gráfico de donaciones por mes, con los últimos doce meses disponibles y los meses sin donaciones en cero.

#### Scenario: Gráfico con varios meses
- **WHEN** la persona donante tiene donaciones en más de un mes
- **THEN** el gráfico muestra una barra por mes con la cantidad correspondiente

#### Scenario: Gráfico con un solo mes
- **WHEN** la persona donante tiene donaciones en un único mes
- **THEN** el gráfico se renderiza igual y muestra ese mes

### Requirement: El panel es una foto y no se actualiza en tiempo real

La vista **Mi impacto** SHALL cargar los datos al ingresar y SHALL NOT usar polling ni WebSocket para actualizarlos automáticamente. La actualización en vivo de progreso, insignias y categoría corresponde a la vista de Incentivos.

#### Scenario: Apertura de la vista
- **WHEN** la persona donante abre Mi impacto
- **THEN** la vista consulta las métricas una vez y las muestra

#### Scenario: Cambio posterior en el backend
- **WHEN** cambian las donaciones o el ranking después de abrir la vista
- **THEN** Mi impacto no se actualiza sola; la persona puede recargar o usar la acción de actualización manual

#### Scenario: Actualización manual
- **WHEN** la persona donante presiona “Actualizar”
- **THEN** la vista vuelve a consultar las métricas y muestra los indicadores y el gráfico actualizados

### Requirement: El panel no duplica el progreso de incentivos

La vista **Mi impacto** SHALL NOT mostrar la misión actual, el progreso de misiones, las insignias ni el cambio de categoría; esos datos pertenecen a la vista de Incentivos y Mi impacto solo ofrece un enlace hacia ella.

#### Scenario: Enlace a Incentivos
- **WHEN** la persona donante quiere ver su progreso, insignias o categoría
- **THEN** Mi impacto le ofrece un enlace a Incentivos en lugar de repetir esos datos

### Requirement: Solo el donante puede ver sus propias métricas

El sistema SHALL validar que la persona donante autenticada solo pueda consultar sus propias métricas y SHALL rechazar el acceso a métricas de otro donante.

#### Scenario: Consulta de las métricas propias
- **WHEN** la persona donante autenticada abre Mi impacto
- **THEN** la vista consulta las métricas asociadas a su propia identidad

#### Scenario: Intento de consultar otro donante
- **WHEN** se solicita el endpoint de métricas con el identificador de otro donante
- **THEN** el sistema rechaza la operación y no devuelve datos ajenos

### Requirement: El panel degrada sin datos falsos

Si el servicio de incentivos no responde, la vista **Mi impacto** SHALL mostrar un mensaje de error y una acción para reintentar, sin inventar métricas ni mostrar datos de ejemplo como si fueran reales.

#### Scenario: Backend caído
- **WHEN** la consulta de métricas falla
- **THEN** la vista muestra un aviso de error y no muestra tarjetas con valores ficticios

#### Scenario: Reintento exitoso
- **WHEN** la persona donante reintenta después de una falla y el servicio responde
- **THEN** la vista muestra las métricas reales

## Purpose

Define la generación programada y a demanda del ranking mensual de personas donantes, ordenado por misiones cumplidas en el período, para que la persona administradora vea un ranking consistente y pueda recalcularlo cuando lo necesite.

## ADDED Requirements

### Requirement: Generación programada del ranking del mes cerrado

El sistema SHALL generar una vez por mes el ranking correspondiente al mes que acaba de cerrar, a las 03:00 del día 1 en la zona `America/Argentina/Buenos_Aires`.

#### Scenario: Cierre de mes
- **WHEN** llega el día 1 a las 03:00 en la zona configurada
- **THEN** el sistema calcula y persiste el ranking del mes anterior, sin depender de la fecha de generación

#### Scenario: Ejecución única mensual
- **WHEN** el job programado se ejecuta
- **THEN** genera un solo ranking para ese período y no se repite cada segundo ni cada día

### Requirement: Generación a demanda por período

La persona administradora SHALL poder recalcular el ranking de un período seleccionado (mes y año) desde la vista de ranking, y el sistema SHALL reemplazar el ranking existente de ese período si ya había uno.

#### Scenario: Recálculo exitoso
- **WHEN** la persona administradora selecciona octubre de 2026 y presiona "Generar ranking"
- **THEN** el sistema recalcula el ranking de octubre de 2026, lo persiste y la vista muestra el resultado actualizado

#### Scenario: Período ya existente
- **WHEN** se genera dos veces el ranking del mismo período
- **THEN** queda un único ranking para ese período y la segunda ejecución reemplaza al anterior, sin duplicados

#### Scenario: Usuario sin permiso
- **WHEN** una persona sin rol administrador intenta generar un ranking
- **THEN** el sistema rechaza la operación

### Requirement: Orden por misiones cumplidas en el período

El ranking SHALL ordenar a las personas donantes por cantidad de misiones completadas en el período, de mayor a menor, e incluir solo a quienes completaron al menos una misión en ese período.

#### Scenario: Mayor cantidad de misiones primero
- **WHEN** un donante completó 3 misiones en el período y otro completó 1
- **THEN** el primero aparece antes en el ranking

#### Scenario: Empate de misiones
- **WHEN** dos donantes completaron la misma cantidad de misiones en el período
- **THEN** se ordenan por cantidad de donaciones del período (mayor primero) y luego por nombre (ascendente)

#### Scenario: Donante sin misiones en el período
- **WHEN** un donante no completó ninguna misión en el período
- **THEN** no aparece en el ranking de ese período

### Requirement: Visualización del ranking por misiones

La vista de ranking SHALL mostrar la cantidad de misiones del período, destacar a los tres primeros puestos y ofrecer la acción de generar el ranking del período seleccionado.

#### Scenario: Tabla con misiones
- **WHEN** la persona administradora abre un período con ranking
- **THEN** ve posición, donante, misiones y donaciones del período, con el podio destacado

#### Scenario: Período sin ranking
- **WHEN** la persona administradora abre un período sin ranking
- **THEN** ve un estado vacío y puede generar el ranking de ese período desde la misma vista

### Requirement: Persistencia consistente del ranking

El sistema SHALL persistir el período del ranking de forma que consultas repetidas del mismo mes devuelvan el mismo resultado y los meses anteriores sigan disponibles.

#### Scenario: Consulta repetida
- **WHEN** se consulta el ranking de octubre de 2026 dos veces sin regenerarlo
- **THEN** ambas consultas devuelven el mismo ranking persistido

#### Scenario: Historial
- **WHEN** la persona administradora consulta un mes anterior
- **THEN** el sistema devuelve el ranking persistido de ese período si existe

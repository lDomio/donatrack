## Purpose

Define el comportamiento de búsqueda y filtrado del listado de entidades beneficiarias para la persona donante, incluyendo valores con acentos, de modo que el servicio reciba el texto original y la vista no muestre errores de carga por un encoding incorrecto.

## ADDED Requirements

### Requirement: El filtro por tipo acepta valores con acentos

El sistema SHALL enviar el tipo de entidad elegido de forma que el servicio reciba el texto original, incluyendo acentos, y SHALL mostrar el listado correspondiente sin el mensaje de error de carga.

#### Scenario: Filtro por Educación
- **WHEN** la persona donante selecciona el tipo "Educación" y envía el filtro
- **THEN** la vista muestra el listado de entidades de ese tipo (posiblemente vacío) sin mostrar "No se pudieron cargar las entidades"

#### Scenario: Filtro por un tipo sin acentos
- **WHEN** la persona donante selecciona "Comedores", "Salud", "Refugios" u "ONG"
- **THEN** la vista sigue mostrando el listado correspondiente sin errores

### Requirement: La búsqueda por texto acepta valores con acentos

El sistema SHALL enviar el texto de búsqueda de forma que el servicio reciba el valor original con acentos, sin convertirlo en una cadena codificada literal.

#### Scenario: Búsqueda con acentos
- **WHEN** la persona donante busca un texto que contiene acentos, por ejemplo "Colón"
- **THEN** el servicio recibe "Colón" y la vista muestra las entidades que coinciden, sin resultados vacíos causados por el encoding

#### Scenario: Búsqueda sin acentos
- **WHEN** la persona donante busca un texto sin acentos, por ejemplo "vida"
- **THEN** la búsqueda sigue funcionando igual que antes

### Requirement: Los filtros combinados no se rompen

El sistema SHALL permitir combinar búsqueda y tipo en la misma consulta manteniendo el encoding correcto de ambos valores.

#### Scenario: Búsqueda y tipo con acentos
- **WHEN** la persona donante busca "Colón" y selecciona "Educación"
- **THEN** la vista realiza la consulta con ambos valores originales y no muestra el error de carga

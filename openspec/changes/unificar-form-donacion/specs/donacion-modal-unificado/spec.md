## Purpose

Permitir al donante elegir varios insumos y cantidades limitadas por faltante desde un unico modal compartido entre entidades y detalle de necesidades.

## ADDED Requirements

### Requirement: Modal multi-insumo con cantidad limitada por fila
El sistema SHALL mostrar por cada necesidad un checkbox de seleccion y un control de cantidad habilitado solo si esta tildada, con minimo 1 y maximo igual al faltante (requerida menos donada).

#### Scenario: Fila no seleccionada
- **WHEN** el insumo no esta tildado
- **THEN** su control de cantidad esta deshabilitado y no se envia

#### Scenario: Fila seleccionada valida
- **WHEN** el usuario tilda Aceite (faltan 48) y pone cantidad 5 con unidad kg
- **THEN** la fila es valida y el boton muestra el conteo actualizado

#### Scenario: Cantidad excede faltante
- **WHEN** el usuario pone cantidad mayor al faltante
- **THEN** la fila marca error, el boton Confirmar se deshabilita y no se envia

#### Scenario: Insumo completo
- **WHEN** el faltante es menor o igual a cero
- **THEN** el checkbox esta deshabilitado con etiqueta de completo

### Requirement: Unidad de medida por fila
El sistema SHALL ofrecer por fila un selector de unidad con lista cerrada y preseleccionar la unidad del catalogo cuando la subcategoria la trae.

#### Scenario: Unidad sugerida del catalogo
- **WHEN** la subcategoria trae unidadDeMedida L
- **THEN** el selector arranca en L con hint de sugerida y permite cambiarla

### Requirement: Confirmacion crea donacion multi-bien
El sistema SHALL enviar al confirmar un POST con entidad, mensaje y contacto globales mas la lista de bienes seleccionados con nombre, cantidad, subcategoriaId y unidad.

#### Scenario: Confirmacion exitosa
- **WHEN** hay al menos un insumo valido y se confirma
- **THEN** se crea la donacion, se cierra el modal y se actualizan las metricas

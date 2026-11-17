## 1. DTOs de vista

- [x] 1.1 Crear `dto/DonacionVistaDto.java` como record con los campos de la sección D2 de `design.md`, y verificar que compila con `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn -q -pl front-end-app -o compile`
- [x] 1.2 Crear `dto/MetricaVistaDto.java` y `dto/NecesidadResumenDto.java` como records, y verificar la misma compilación
- [x] 1.3 Crear `dto/EntidadVistaDto.java` como record, con `List<MetricaVistaDto>` y `List<NecesidadResumenDto>`, y verificar la misma compilación
- [x] 1.4 Crear `dto/PuntoCercanoDto.java` y `dto/PinMapaDto.java` como records, y verificar la misma compilación
- [x] 1.5 Revisar los nombres de campo de los 5 records contra `EntidadBeneficiariaResponse` y `NecesidadDto` del `donaciones-service`, y anotar en un comentario cualquier campo que hoy no exista en el DTO real

## 2. Mapa de donaciones

- [x] 2.1 Agregar al controller el handler de `/personaDonante/mapaDonaciones` con `titulo` y `vistaActual`, más los mocks de `donacionEnRuta`, `puntosCercanos` y `pines`, y verificar que compila
- [x] 2.2 Escribir `mapaDonaciones.html`: fragmento `estilosExtra` con el `<link>` a su CSS, y fragmento `contenido` con `.seccionPrincipal`, `.encabezadoSeccion` y `.botonVolverMisDonaciones` enlazado a `/personaDonante/misDonaciones`
- [x] 2.3 Renderizar `.tarjetaDonacionEnRuta` con `.insigniaEstadoEnRuta`, `.tituloRutaDonacion`, `.detalleRutaDonacion`, `.estadoTiempoEstimado`, `.etiquetaTiempo` y `.valorTiempo`
- [x] 2.4 Renderizar `.barraFiltrosMapa` con `.cajaBusquedaMapa` (`.iconoBusquedaMapa`, `.campoUbicacion`) y `.chipsTipoPunto` con un `.chipFunto` por tipo, marcando uno `.activo`
- [x] 2.5 Renderizar `.visorMapaContenedor` con `img.imagenMapaPrincipal` apuntando a `/img/mapa.jpg`, y el `th:each` de `.pinMapa` aplicando `th:classappend="${pin.clasePosicion}"`, con `.iconoPinCamion` y `.pulsoRadar` solo en el pin de camión
- [x] 2.6 Renderizar el `.globitoPopup` de cada pin con nombre, `.etiquetaPopupEnRuta` o `.etiquetaPopupEntregado`, `.etiquetaTiempo`, `.valorTiempo`, `.estadoTiempoEstimado`, `.subtextoPopup` y `.enlacePopup`
- [x] 2.7 Renderizar `.seccionPuntosCercanos` con `.tituloPuntosCercanos` y `.cuadriculaPuntos`, una `.tarjetaPuntoMapa` por punto aplicando `.destacada` a los destacados y ordenándolos primero
- [x] 2.8 Verificar en el navegador que los 3 pines se posicionan según su clase, que los globitos aparecen al hover, y que el control de volver lleva a `misDonaciones`

## 3. Entidades beneficiarias

- [x] 3.1 Agregar al controller el handler de `/personaDonante/entidadesBeneficiarias` con `titulo`, `vistaActual`, `tiposEntidad` y el mock de `entidades`, y verificar que compila
- [x] 3.2 Escribir `entidadesBeneficiarias.html`: fragmento `estilosExtra` y fragmento `contenido` con `.seccionPrincipal` y `.encabezadoSeccion`
- [x] 3.3 Renderizar `.tarjetaImpactoRapido` con `.iconoImpacto`, `.numeroImpacto` y `.etiquetaImpacto`
- [x] 3.4 Renderizar `.seccionFiltros` con `.cajaBusqueda` (`.iconoBusqueda`, `.campoEntrada`), `.cajaFiltroSelect` (`.iconoFiltro`, `.selectorFiltro`) y `.etiquetasFiltroRapido` con un `.etiquetaFiltro` por tipo, uno `.activa`
- [x] 3.5 Renderizar `.cuadriculaEntidades` con una `.tarjetaEntidad` por entidad: `.contenedorImagenEntidad` con `img.imagenEntidad` solo si hay imagen, y `.insigniaTipo` con la clase de insignia según el tipo
- [x] 3.6 Renderizar `.cuerpoTarjetaEntidad` con `.encabezadoTarjeta` (`.nombreEntidad`, `.ubicacionEntidad`), `.descripcionEntidad` y `.metricasEntidad` con un `.metricaItem` por métrica, aplicando `.alertaCritica` y `th:style` de ancho a `.progresoRelleno`
- [x] 3.7 Renderizar `.pieTarjetaEntidad` con `.etiquetaImpacto` (`.iconoImpacto`, `.numeroImpacto`) y `.botonVerNecesidades` enlazado a `/personaDonante/verNecesidadEntidad/{id}`
- [x] 3.8 Implementar el modal por entidad: `.bloqueDonacionDirecta > .botonDonarAhora` con `th:href` a `#modalDonar{id}`, y `.modalContenedor` con `th:id` y el `th:each` de `.listaNecesidades` (`.itemNecesidad` con `.urgente`, `.infoNecesidad`, `.nombreNecesidad`, `.progresoNecesidad`, `.barraProgreso`, `.progresoRelleno` con ancho inline)
- [x] 3.9 Verificar en el navegador que con 2+ entidades cada modal se abre y cierra por separado, que el fragmento de la URL cambia a `#modalDonar{id}`, y que el botón de necesidades lleva a la ruta con el id correcto

## 4. Detalle de necesidades de entidad

- [x] 4.1 Cambiar el mapeo de `/personaDonante/verNecesidadEntidad` a `/personaDonante/verNecesidadEntidad/{id}` recibiendo `Long id`, y verificar que el mapeo viejo ya no existe
- [x] 4.2 Agregar al controller el mock de `entidad` y `categorias` para ese handler, y verificar que compila
- [x] 4.3 Escribir `verNecesidadEntidad.html`: fragmento `estilosExtra` y `.seccionPrincipal` con `.bloqueNavegacionSuperior > .botonVolverEntidades` enlazado a `/personaDonante/entidadesBeneficiarias`
- [x] 4.4 Renderizar `.tarjetaEntidadDetalle` con `.contenedorImagenDetalle` (`.imagenDetalle`), `.insigniaVerificado` solo si está verificada, e `.infoEntidadDetalle` con `.tipoEntidadBadging`, `.tituloEntidadDetalle`, `.ubicacionEntidadDetalle` y `.descripcionEntidadDetalle`
- [x] 4.5 Renderizar `.resumenImpactoEntidad` con un `.itemResumen` por cifra, y `.encabezadoPaginaNecesidades` con título y subtítulo
- [x] 4.6 Renderizar `.cuadriculaNecesidadesDetalle` con una `.tarjetaCategoriaNecesidad` por categoría: `.cabeceraCategoriaNecesidad` con `.iconoCategoria` y su clase de color, `.tituloCategoriaNecesidad`, `.frecuenciaEtiqueta` solo si tiene recurrencia, y `.descripcionCategoria`
- [x] 4.7 Renderizar `.listaInsumos` con un `.itemInsumo` por insumo, aplicando `.urgente` y `.completado`, con `.encabezadoInsumo` (`.nombreInsumo`, `.progresoTexto` con modificadores) y `.contenedorBarra > .barraRelleno` con clase de color y ancho inline
- [x] 4.8 Renderizar `.pieCategoriaNecesidad > .botonDonarCategoria` con `th:href` a `#modalFormulario{id}`, y el `.modalContenedor` con `th:id` y `.formularioDonacion` (`.grupoCampo` con `.etiquetaCampo` y `.campoEntrada`, más `.botonConfirmarModal`)
- [x] 4.9 Renderizar `.barraFlotanteMovil > .botonMovilFijo`
- [x] 4.10 Verificar en el navegador que `/personaDonante/verNecesidadEntidad` sin id no renderiza la vista, que `/1` sí, y que con 2+ categorías cada formulario se abre y cierra por separado

## 5. Mis donaciones sobre el DTO compartido

- [x] 5.1 Reemplazar los `List.of()` vacíos de `misDonaciones` por el mock de `List<DonacionVistaDto>`, y verificar que compila
- [x] 5.2 Ajustar `misDonaciones.html` a los nombres de campo de `DonacionVistaDto`, conservando los class names del CSS existentes
- [x] 5.3 Verificar en el navegador que el estado en tránsito y el entregado se distinguen, que el resto se renderiza legible, y que una donación sin imagen no renderiza el elemento `img`

## 6. Verificación integral

- [x] 6.1 Compilar el módulo completo con `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn -q -pl front-end-app -o compile` y confirmar que no hay errores
- [x] 6.2 Recorrer las 4 rutas en el navegador y confirmar que cada elemento con clase del CSS existe en el DOM, es decir, que ninguna regla queda sin aplicar
- [x] 6.3 Confirmar que no se modificó ningún archivo bajo `static/css/` ni bajo `donaciones-service/`, revisando `git status`
- [x] 6.4 Correr `openspec validate vistas-persona-donante --strict` y corregir cualquier hallazgo

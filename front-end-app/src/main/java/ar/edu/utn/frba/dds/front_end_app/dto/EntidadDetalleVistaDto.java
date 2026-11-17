package ar.edu.utn.frba.dds.front_end_app.dto;

import java.util.List;

/**
 * Bundle con todo lo que necesita el template del detalle de entidad:
 * cabecera + categorias de necesidades.
 */
public record EntidadDetalleVistaDto(
        EntidadDetalleDto entidad,
        List<CategoriaNecesidadDto> categorias
) {
}

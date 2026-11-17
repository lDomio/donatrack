package ar.edu.utn.frba.dds.front_end_app.dto;

import java.util.List;

public record EntidadDetalleDto(
        Long id,
        String razonSocial,
        String tipo,
        String ubicacion,
        String descripcion,
        String imagenUrl,
        boolean verificada,
        List<CifraImpactoDto> impacto
) {
}

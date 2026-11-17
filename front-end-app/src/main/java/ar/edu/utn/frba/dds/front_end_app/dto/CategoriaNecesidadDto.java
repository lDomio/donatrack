package ar.edu.utn.frba.dds.front_end_app.dto;

import java.util.List;

public record CategoriaNecesidadDto(
        String titulo,
        String descripcion,
        String icono,
        String claseColorIcono,
        String frecuencia,
        List<InsumoNecesidadDto> insumos
) {
}

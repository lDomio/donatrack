package ar.edu.utn.frba.dds.front_end_app.dto;

public record InsumoNecesidadDto(
        String nombre,
        double cantidadRequerida,
        double cantidadDonada,
        int porcentaje,
        boolean urgente,
        boolean completado,
        String claseColorBarra,
        Long subcategoriaId,
        String unidadSugerida,
        int faltante
) {
}

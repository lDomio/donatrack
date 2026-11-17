package ar.edu.utn.frba.dds.front_end_app.dto;

public record NecesidadResumenDto(
        String nombre,
        int cantidadRequerida,
        int cantidadDonada,
        int porcentaje,
        boolean urgente,
        String claseColorProgreso,
        Long subcategoriaId,
        String unidadSugerida,
        int faltante
) {
}

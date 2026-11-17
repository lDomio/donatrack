package ar.edu.utn.frba.dds.front_end_app.dto;

public record MetricaVistaDto(
        String nombre,
        int cantidadRequerida,
        int cantidadDonada,
        int porcentaje,
        boolean critica,
        Long subcategoriaId,
        String unidadSugerida,
        int faltante
) {
}

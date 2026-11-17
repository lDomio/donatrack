package ar.edu.utn.frba.dds.front_end_app.dto;

import java.util.List;

public record EntidadVistaDto(
        Long id,
        String razonSocial,
        String tipo,
        String ubicacion,
        String descripcion,
        String imagenUrl,
        List<MetricaVistaDto> metricas,
        int progresoPorcentaje,
        String claseColorProgreso,
        int impacto,
        String iconoImpacto,
        List<NecesidadResumenDto> necesidades
) {
}

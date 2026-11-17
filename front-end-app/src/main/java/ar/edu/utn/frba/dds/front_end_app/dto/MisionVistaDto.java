package ar.edu.utn.frba.dds.front_end_app.dto;

public record MisionVistaDto(
        Long id,
        String titulo,
        String descripcion,
        String categoria,
        String imagenUrl,
        int cantidadActual,
        int cantidadObjetivo,
        int porcentaje,
        String estado,
        String insigniaOtorgada,
        String advertencia
) {
}

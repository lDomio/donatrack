package ar.edu.utn.frba.dds.front_end_app.dto;

public record PuntoCercanoDto(
        Long id,
        String nombre,
        String direccion,
        String horario,
        boolean destacado,
        String etiquetaEstado,
        String tiempoEstimado
) {
}

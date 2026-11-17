package ar.edu.utn.frba.dds.front_end_app.dto;

public record InsigniaVistaDto(
        Long id,
        String nombre,
        String descripcion,
        String icono,
        String colorClase,
        boolean obtenida,
        boolean visiblePerfil
) {
}

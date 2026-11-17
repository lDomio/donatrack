package ar.edu.utn.frba.dds.front_end_app.dto;

public record CategoriaDonanteDto(
        String nombre,
        String icono,
        String colorClase,
        boolean alcanzada,
        boolean actual
) {
}

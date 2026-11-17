package ar.edu.utn.frba.dds.front_end_app.dto;

public record SubcategoriaResponse(
        Long id,
        String nombre,
        Long categoriaId,
        String categoriaNombre,
        String unidadDeMedida
) {
}

package ar.edu.utn.frba.dds.donaciones.main.dtos.categoria;

public record SubcategoriaResponse(
    Long id,
    String nombre,
    Long categoriaId,
    String categoriaNombre,
    String unidadDeMedida
) {
}

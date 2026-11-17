package ar.edu.utn.frba.dds.donaciones.main.dtos.categoria;

public record SubcategoriaRequest(
    String nombre,
    Long categoriaId,
    String unidadDeMedida
) {
}

package ar.edu.utn.frba.dds.front_end_app.dto;

public record SubcategoriaRequest(
    String nombre,
    Long categoriaId,
    String unidadDeMedida
) {
}

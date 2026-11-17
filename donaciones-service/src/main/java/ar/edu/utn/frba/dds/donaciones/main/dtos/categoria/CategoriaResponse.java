package ar.edu.utn.frba.dds.donaciones.main.dtos.categoria;

import java.util.List;

public record CategoriaResponse(
    Long id,
    String nombre,
    List<SubcategoriaSimpleResponse> subcategorias
) {
}

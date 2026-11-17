package ar.edu.utn.frba.dds.front_end_app.dto;

import java.util.List;

public record CategoriaResponse(
    Long id,
    String nombre,
    List<SubcategoriaSimpleResponse> subcategorias
) {
}

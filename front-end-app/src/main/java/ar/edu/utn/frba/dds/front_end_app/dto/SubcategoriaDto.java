package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubcategoriaDto(
        Long id,
        String nombre,
        String unidadDeMedida
) {
}

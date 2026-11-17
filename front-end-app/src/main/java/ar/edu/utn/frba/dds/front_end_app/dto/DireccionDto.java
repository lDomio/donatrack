package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DireccionDto(
        String calle,
        Integer altura,
        String ciudad,
        Double latitud,
        Double longitud
) {
}

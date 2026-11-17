package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Espejo de {@code incentivos-service}: usado tanto para
 * {@code GET /incentivos/{id}/insignias} como para el campo anidado {@code insignia}
 * de {@link MisionResponse} (ahi el backend serializa la entidad JPA completa;
 * ignoramos lo que no sea {@code descripcion}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InsigniaResponse(String descripcion) {
}

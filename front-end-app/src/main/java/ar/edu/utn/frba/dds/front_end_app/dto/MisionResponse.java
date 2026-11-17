package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

/** Espejo de {@code GET /incentivos/{id}/misiones} (incentivos-service): solo misiones completadas. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MisionResponse(InsigniaResponse insignia, LocalDate fecha) {
}

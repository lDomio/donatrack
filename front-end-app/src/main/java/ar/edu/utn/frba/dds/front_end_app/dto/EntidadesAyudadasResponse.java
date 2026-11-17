package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Espejo del shape JSON de {@code donaciones-service} para
 * {@code GET /donaciones-service/donaciones/donante/{id}/entidades-ayudadas-este-mes}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EntidadesAyudadasResponse(
        Long donanteId,
        int anio,
        int mes,
        long cantidad
) {
}

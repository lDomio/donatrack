package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Espejo del shape JSON de {@code donaciones-service} para
 * {@code GET /donaciones-service/entidades-beneficiarias}.
 * Ignora campos del backend que esta vista no necesita (contactos, representantes).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EntidadBeneficiariaResponse(
        Long id,
        String razonSocial,
        String tipo,
        List<NecesidadResponse> necesidades,
        DireccionDto direccion
) {
    public EntidadBeneficiariaResponse(Long id, String razonSocial, String tipo,
                                       List<NecesidadResponse> necesidades) {
        this(id, razonSocial, tipo, necesidades, null);
    }
}

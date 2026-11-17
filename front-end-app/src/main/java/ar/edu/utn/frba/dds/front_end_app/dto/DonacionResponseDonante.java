package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Espejo del shape JSON de {@code donaciones-service} para
 * {@code GET /donaciones-service/donaciones/donante/{idDonante}}.
 * Ignora campos del backend que esta vista no necesita (p.ej. ids de asignacion).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DonacionResponseDonante(
        Long id,
        List<BienResponse> bienes,
        String descripcionGeneral,
        TipoDeEstado estado,
        SubcategoriaResponse subcategoria,
        String fotoEntrega
) {
    public DonacionResponseDonante(
            Long id,
            List<BienResponse> bienes,
            String descripcionGeneral,
            TipoDeEstado estado,
            SubcategoriaResponse subcategoria
    ) {
        this(id, bienes, descripcionGeneral, estado, subcategoria, null);
    }
}

package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Espejo minimo del shape JSON de {@code NecesidadDto} de {@code donaciones-service}.
 * Solo trae lo que la vidriera necesita; el resto se ignora.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NecesidadResponse(
        Long id,
        SubcategoriaDto subcategoria,
        String tipoNecesidad,
        Double cantidad,
        Double cantidadDonada,
        String periodoRecurrencia
) {
  public NecesidadResponse(Long id, SubcategoriaDto subcategoria, String tipoNecesidad,
      Double cantidad, Double cantidadDonada) {
    this(id, subcategoria, tipoNecesidad, cantidad, cantidadDonada, null);
  }
}

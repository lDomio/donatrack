package ar.edu.utn.frba.dds.front_end_app.dto;

import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.RepresentanteDTO;
import java.util.List;

public record JuridicoCreateRequest(
    String tipo,
    String razonSocial,
    String tipoJuridica,
    String rubro,
    List<MedioDeContactoCreateDto> contactos,
    List<RepresentanteDTO> representantes
) {
  public JuridicoCreateRequest(
      String razonSocial,
      String tipoJuridica,
      String rubro,
      List<MedioDeContactoCreateDto> contactos,
      List<RepresentanteDTO> representantes
  ) {
    this("JURIDICA", razonSocial, tipoJuridica, rubro, contactos, representantes);
  }
}

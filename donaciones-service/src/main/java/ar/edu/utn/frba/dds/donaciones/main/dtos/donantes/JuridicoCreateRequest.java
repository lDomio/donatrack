package ar.edu.utn.frba.dds.donaciones.main.dtos.donantes;

import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;

import java.util.List;

public record JuridicoCreateRequest(
    String razonSocial,
    String tipoJuridica,
    String rubro,
    List<MedioDeContactoDTO> contactos,
    List<RepresentanteDTO> representantes
) implements DonanteCreateRequest {
}

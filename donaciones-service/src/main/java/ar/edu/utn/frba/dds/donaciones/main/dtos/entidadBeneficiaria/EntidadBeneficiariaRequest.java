package ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.MedioDeContactoDTO;
import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;

import java.util.List;

public record EntidadBeneficiariaRequest(
    String razonSocial,
    String tipoEntidad,
    String calle,
    Integer altura,
    List<MedioDeContactoDTO> contactos,
    List<RepresentanteDTO> representantes,
    Long idUsuarioAuth
) {
}

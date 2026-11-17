package ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria;

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

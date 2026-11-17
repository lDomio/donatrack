package ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.MedioDeContactoDTO;
import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;

import java.util.List;

public record EntidadBeneficiariaResponse(
    Long id,
    String razonSocial,
    String tipo,
    List<MedioDeContactoDTO> contactos,
    List<RepresentanteDTO> representantes,
    List<NecesidadDto> necesidades,
    List<DonacionResponse> donacionesAsignadas,
    DireccionDto direccion
) {
}


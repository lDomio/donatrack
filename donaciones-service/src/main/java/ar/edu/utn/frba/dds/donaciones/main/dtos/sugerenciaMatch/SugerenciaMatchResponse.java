package ar.edu.utn.frba.dds.donaciones.main.dtos.sugerenciaMatch;


import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadDto;

import java.util.List;

public record SugerenciaMatchResponse(
    Long id,
    Long idDonacion,
    List<NecesidadDto> necesidadesSugeridas,
    Boolean yaFueSeleccionadoPorElAdministrador
) {

}

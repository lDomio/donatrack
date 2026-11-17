package ar.edu.utn.frba.dds.front_end_app.dto.donante;

import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.NecesidadDTO;
import java.util.List;

public record SugerenciaMatchResponse(
    Long id,
    Long idDonacion,
    List<NecesidadDTO> necesidadesSugeridas,
    Boolean yaFueSeleccionadoPorElAdministrador
) {
}

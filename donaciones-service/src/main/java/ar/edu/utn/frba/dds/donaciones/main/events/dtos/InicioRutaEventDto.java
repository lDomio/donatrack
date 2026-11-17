package ar.edu.utn.frba.dds.donaciones.main.events.dtos;

import java.util.List;
import java.util.Set;

public record InicioRutaEventDto(Long rutaId, List<MensajeEntidadBeneficiariaDto> mensajeEntidadesBeneficiarias, List<MensajeDonanteDto> mensajeDonantes) {
}

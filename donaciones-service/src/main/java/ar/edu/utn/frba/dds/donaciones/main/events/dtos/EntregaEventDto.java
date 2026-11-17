package ar.edu.utn.frba.dds.donaciones.main.events.dtos;

import java.util.List;

public record EntregaEventDto(Long entregaId, List<MensajeEntidadBeneficiariaDto> mensajeEntidadesBeneficiarias, List<MensajeDonanteDto> mensajeDonantes) {
}

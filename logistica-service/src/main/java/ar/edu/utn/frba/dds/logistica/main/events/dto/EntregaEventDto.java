package ar.edu.utn.frba.dds.logistica.main.events.dto;

import java.util.List;

public record EntregaEventDto(Long entregaId, List<MensajeEntidadBeneficiariaDto> mensajeEntidadesBeneficiarias, List<MensajeDonanteDto> mensajeDonantes) {
}

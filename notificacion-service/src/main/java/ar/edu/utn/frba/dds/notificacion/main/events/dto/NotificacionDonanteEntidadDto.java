package ar.edu.utn.frba.dds.notificacion.main.events.dto;

import ar.edu.utn.frba.dds.notificacion.main.services.dtos.MedioDeContactoDto;

public record NotificacionDonanteEntidadDto(MedioDeContactoDto medioDeContacto, String mensaje) {
}

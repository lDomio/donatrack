package ar.edu.utn.frba.dds.donaciones.main.events.dtos;

import java.util.List;

public record NotificacionDonanteEvent(
        String nombre,
        String mensaje,
        List<MedioDeContactoDto> mediosDeContacto
) {
}

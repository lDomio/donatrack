package ar.edu.utn.frba.dds.donaciones.main.events.dtos;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;

public record InicioRutaEventDtoSalida(MedioDeContacto medioDeContacto, String mensaje) {
}

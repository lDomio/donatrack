package ar.edu.utn.frba.dds.incentivos.main.dtos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;

import java.util.List;

public record CrearDonanteRequest(
    Long id,
    Long idUsuarioAuth,
    String nombreUsuario,
    List<MedioDeContactoRequest> contactos
) {
    public record MedioDeContactoRequest(String tipo, String valor) {}
}

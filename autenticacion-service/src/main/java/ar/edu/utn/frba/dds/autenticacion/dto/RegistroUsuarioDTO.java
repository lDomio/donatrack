package ar.edu.utn.frba.dds.autenticacion.dto;

public record RegistroUsuarioDTO(
    String username,
    String password,
    String nombre,
    String rol
) {
}

package ar.edu.utn.frba.dds.front_end_app.dto;

public record RegistroAuthRequest(
    String username,
    String password,
    String nombre,
    String rol
) {
}

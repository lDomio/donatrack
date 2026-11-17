package ar.edu.utn.frba.dds.autenticacion.dto;

public record UsuarioResponseDTO(
    Long id,
    String username,
    String rol
) {
}

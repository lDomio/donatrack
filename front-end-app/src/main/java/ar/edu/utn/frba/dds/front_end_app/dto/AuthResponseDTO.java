package ar.edu.utn.frba.dds.front_end_app.dto;

public record AuthResponseDTO(
    String accessToken,
    String refreshToken
) {
}

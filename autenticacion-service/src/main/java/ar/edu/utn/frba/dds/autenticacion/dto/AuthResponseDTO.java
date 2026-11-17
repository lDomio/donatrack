package ar.edu.utn.frba.dds.autenticacion.dto;

import lombok.Builder;

@Builder
public record AuthResponseDTO(
    String accessToken,
    String refreshToken) {
}

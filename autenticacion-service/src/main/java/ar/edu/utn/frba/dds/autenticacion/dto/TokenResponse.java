package ar.edu.utn.frba.dds.autenticacion.dto;

public record TokenResponse
    (String accessToken,
    String refreshToken)
{
}

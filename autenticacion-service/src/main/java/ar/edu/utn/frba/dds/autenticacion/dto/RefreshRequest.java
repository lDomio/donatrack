package ar.edu.utn.frba.dds.autenticacion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshRequest
    (@JsonProperty("refreshToken") String refreshToken)
{
}

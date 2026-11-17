package ar.edu.utn.frba.dds.logistica.main.dtos.error;

import java.time.Instant;

public record ErrorResponse(String error, String message, Instant timestamp) {
}

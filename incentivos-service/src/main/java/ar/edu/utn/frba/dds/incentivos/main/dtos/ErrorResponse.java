package ar.edu.utn.frba.dds.incentivos.main.dtos;

import java.time.Instant;

public record ErrorResponse(String error, String message, Instant timestamp) {
}

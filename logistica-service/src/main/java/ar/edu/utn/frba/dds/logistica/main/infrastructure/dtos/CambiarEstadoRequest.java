package ar.edu.utn.frba.dds.logistica.main.infrastructure.dtos;

import java.time.LocalDate;

public record CambiarEstadoRequest(
    String motivo,
    LocalDate fechaNuevoEstado,
    String nuevoEstado,
    String autorDelCambio
) {}

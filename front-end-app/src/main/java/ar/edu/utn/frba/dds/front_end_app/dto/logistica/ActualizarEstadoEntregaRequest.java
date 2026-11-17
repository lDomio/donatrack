package ar.edu.utn.frba.dds.front_end_app.dto.logistica;

import java.time.LocalDate;

public record ActualizarEstadoEntregaRequest(
    String nuevoEstado,
    String observaciones,
    LocalDate fecha
) {}

package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import java.time.LocalDate;

public record ActualizarEstadoEntregaRequest(
    String nuevoEstado,
    String observaciones,
    LocalDate fecha
) {}

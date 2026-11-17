package ar.edu.utn.frba.dds.logistica.main.dtos.transporte;

public record ActualizarEstadoRutaRequest(
    String nuevoEstado,
    String responsableDelCambio
) {}

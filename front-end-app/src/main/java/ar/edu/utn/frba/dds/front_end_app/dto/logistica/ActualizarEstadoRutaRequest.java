package ar.edu.utn.frba.dds.front_end_app.dto.logistica;

public record ActualizarEstadoRutaRequest(
    String nuevoEstado,
    String responsableDelCambio
) {}

package ar.edu.utn.frba.dds.donaciones.main.dtos.donacion;

import java.time.LocalDate;

public record CambiarEstadoRequest(
    String motivo,
    LocalDate fechaNuevoEstado,
    String nuevoEstado,
    String autorDelCambio,
    String fotoEntrega
) {
}

package ar.edu.utn.frba.dds.front_end_app.dto.donante;

import java.time.LocalDate;

public record CambiarEstadoRequest(
    String motivo,
    LocalDate fechaNuevoEstado,
    String nuevoEstado,
    String autorDelCambio,
    String fotoEntrega
) {
}

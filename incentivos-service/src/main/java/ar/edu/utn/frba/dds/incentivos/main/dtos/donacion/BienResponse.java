package ar.edu.utn.frba.dds.incentivos.main.dtos.donacion;

import java.time.LocalDate;

public record BienResponse(
    Long id,
    String descripcion,
    Double cantidad,
    LocalDate fechaVencimiento
) {
}

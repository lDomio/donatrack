package ar.edu.utn.frba.dds.incentivos.main.dtos.donacion;

import java.time.LocalDate;
import java.util.List;

public record DonacionResponse(
    Long id,
    List<BienResponse> bienes,
    String descripcionGeneral,
    String estado,
    LocalDate ultimaActualizacion,
    String categoria
) {
}

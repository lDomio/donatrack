package ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria;

import java.time.LocalDate;

public record PeriodoDTO(
    Long id,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Double cantidadDonada
) {
}

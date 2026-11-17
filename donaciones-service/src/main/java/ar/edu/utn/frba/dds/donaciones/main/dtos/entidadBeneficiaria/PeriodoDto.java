package ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import java.time.LocalDate;
import java.util.List;

public record PeriodoDto (
    Long id,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Double cantidadDonada
){
}

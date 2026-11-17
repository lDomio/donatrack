package ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria;

import java.time.LocalDate;

public record NecesidadRequest(
    Long subcategoriaId,
    Double cantidad,
    String descripcion,
    String tipoNecesidad, // RECURRENTE / EXTRAORDINARIA
    String periodo,       // ISO-8601 duration (ej: P1M)
    LocalDate inicioRecurrencia
) {
}

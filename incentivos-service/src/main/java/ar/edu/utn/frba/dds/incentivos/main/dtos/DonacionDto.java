package ar.edu.utn.frba.dds.incentivos.main.dtos;

import java.time.LocalDate;

public record DonacionDto(
    Long id,
    LocalDate fecha,
    Double cantidadDeBien,
    String categoria,
    LocalDate fechaDeRecepcion,
    Boolean fueRecibida,
    String nombreDeLaEntidadQueRecibe
) {}

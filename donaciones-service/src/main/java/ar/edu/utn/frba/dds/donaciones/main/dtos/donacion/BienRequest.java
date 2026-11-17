package ar.edu.utn.frba.dds.donaciones.main.dtos.donacion;

import java.time.LocalDate;

public record BienRequest(
    String nombre,
    String descripcion,
    Double cantidad,
    String unidadDeMedida,
    Long subcategoriaId,
    String estado,
    LocalDate fechaVencimiento,
    String urlFoto
) {
}


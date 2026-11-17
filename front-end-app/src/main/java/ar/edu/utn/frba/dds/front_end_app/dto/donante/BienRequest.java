package ar.edu.utn.frba.dds.front_end_app.dto.donante;

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

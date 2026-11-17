package ar.edu.utn.frba.dds.front_end_app.dto;

import java.time.LocalDate;

public record CasoExitoDto(
    Long idDonacion,
    String tituloDonacion,
    String descripcionDonacion,
    String nombreEntidad,
    String direccionEntidad,
    Double latitud,
    Double longitud,
    String fotoEntrega,
    LocalDate fechaEntrega
) {
}

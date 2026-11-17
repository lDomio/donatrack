package ar.edu.utn.frba.dds.donaciones.main.dtos.donacion;

import java.time.LocalDate;

public record CasoExitoResponse(
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

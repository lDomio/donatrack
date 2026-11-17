package ar.edu.utn.frba.dds.donaciones.main.dtos.donacion;

import java.util.List;

public record CrearDonacionRequest(
    Long idDonante,
    String descripcionGeneral,
    List<BienRequest> bienes
) {
}

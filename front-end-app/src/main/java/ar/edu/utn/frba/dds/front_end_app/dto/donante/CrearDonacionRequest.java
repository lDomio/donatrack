package ar.edu.utn.frba.dds.front_end_app.dto.donante;

import java.util.List;

public record CrearDonacionRequest(
    Long idDonante,
    String descripcionGeneral,
    List<BienRequest> bienes
) {
}

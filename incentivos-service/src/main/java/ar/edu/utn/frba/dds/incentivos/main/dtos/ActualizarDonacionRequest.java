package ar.edu.utn.frba.dds.incentivos.main.dtos;

public record ActualizarDonacionRequest(
    Long idDonacion,
    String razonSocialEntidadBeficiaria
) {
}

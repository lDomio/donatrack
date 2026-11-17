package ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria;

public record DireccionDto(
    String calle,
    Integer altura,
    String ciudad,
    Double latitud,
    Double longitud
) {
}

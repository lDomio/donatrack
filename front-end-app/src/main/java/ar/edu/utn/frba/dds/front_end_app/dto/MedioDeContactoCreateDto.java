package ar.edu.utn.frba.dds.front_end_app.dto;

public record MedioDeContactoCreateDto(
    String tipo,
    String valor,
    Boolean esPreferido
) {
}

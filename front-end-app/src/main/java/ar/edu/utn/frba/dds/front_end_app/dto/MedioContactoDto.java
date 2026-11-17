package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MedioContactoDto(
    Long id,
    @JsonAlias({"tipoDeContacto", "tipo"})
    TipoDeContactoDto tipoDeContactoDto,
    @JsonAlias({"valorDelContacto", "ValorDelContacto", "valor"})
    String valorDelContacto,
    Boolean esPreferido
) {
}

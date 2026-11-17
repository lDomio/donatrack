package ar.edu.utn.frba.dds.front_end_app.dto.donante;

import ar.edu.utn.frba.dds.front_end_app.dto.MedioContactoDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DonanteDto(
    Long id,
    String nombre,
    List<MedioContactoDto> contactos,
    String tipo,
    Boolean estaActivo,
    List<RepresentanteDonanteDto> representantes
) {
  public DonanteDto(
      Long id,
      String nombre,
      List<MedioContactoDto> contactos,
      String tipo,
      Boolean estaActivo
  ) {
    this(id, nombre, contactos, tipo, estaActivo, List.of());
  }
}

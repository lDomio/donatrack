package ar.edu.utn.frba.dds.donaciones.main.dtos.donantes;

import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;
import java.util.List;

public record DonanteResponse(
    Long id,
    String nombre,
    List<MedioDeContactoDTO> contactos,
    String tipo,
    Boolean estaActivo,
    List<RepresentanteDTO> representantes
) {
  public DonanteResponse(
      Long id,
      String nombre,
      List<MedioDeContactoDTO> contactos,
      String tipo,
      Boolean estaActivo
  ) {
    this(id, nombre, contactos, tipo, estaActivo, List.of());
  }
}

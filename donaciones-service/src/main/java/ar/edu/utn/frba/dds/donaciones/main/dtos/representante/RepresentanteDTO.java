package ar.edu.utn.frba.dds.donaciones.main.dtos.representante;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.MedioDeContactoDTO;
import java.util.List;

public record RepresentanteDTO(
    Long id,
    String nombre,
    MedioDeContactoDTO contacto,
    List<MedioDeContactoDTO> contactos
) {
  public RepresentanteDTO(String nombre, MedioDeContactoDTO contacto) {
    this(null, nombre, contacto, contacto != null ? List.of(contacto) : List.of());
  }

  public RepresentanteDTO(Long id, String nombre, List<MedioDeContactoDTO> contactos) {
    this(id, nombre, (contactos != null && !contactos.isEmpty()) ? contactos.get(0) : null, contactos != null ? contactos : List.of());
  }
}

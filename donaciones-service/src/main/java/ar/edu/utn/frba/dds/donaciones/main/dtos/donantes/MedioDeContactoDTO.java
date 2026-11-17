package ar.edu.utn.frba.dds.donaciones.main.dtos.donantes;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.TipoDeContacto;

public record MedioDeContactoDTO(
    TipoDeContacto tipo,
    String valor,
    Boolean esPreferido
) {
  public MedioDeContactoDTO(TipoDeContacto tipo, String valor) {
    this(tipo, valor, false);
  }
}

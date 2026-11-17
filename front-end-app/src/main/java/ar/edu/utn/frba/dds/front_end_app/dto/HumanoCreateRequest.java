package ar.edu.utn.frba.dds.front_end_app.dto;

import java.time.LocalDate;
import java.util.List;

public record HumanoCreateRequest(
    String tipo,
    String nombre,
    String apellido,
    LocalDate fechaDeNacimiento,
    String genero,
    String numeroDeDocumento,
    String calle,
    Integer altura,
    Long idUsuarioAuth,
    List<MedioDeContactoCreateDto> contactos
) {
  public HumanoCreateRequest(
      String nombre,
      String apellido,
      LocalDate fechaDeNacimiento,
      String genero,
      String numeroDeDocumento,
      String calle,
      Integer altura,
      Long idUsuarioAuth,
      List<MedioDeContactoCreateDto> contactos
  ) {
    this("HUMANA", nombre, apellido, fechaDeNacimiento, genero, numeroDeDocumento, calle, altura, idUsuarioAuth, contactos);
  }
}

package ar.edu.utn.frba.dds.donaciones.main.dtos.donantes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public record HumanoCreateRequest(
    String nombre,
    String apellido,
    LocalDate fechaDeNacimiento,
    String genero,
    String numeroDeDocumento,
    String calle,
    Integer altura,
    Long idUsuarioAuth,
    List<MedioDeContactoDTO> contactos
) implements DonanteCreateRequest {
  public HumanoCreateRequest(
      String nombre,
      String apellido,
      LocalDate fechaDeNacimiento,
      String genero,
      String numeroDeDocumento,
      List<MedioDeContactoDTO> contactos
  ) {
    this(nombre, apellido, fechaDeNacimiento, genero, numeroDeDocumento, null, null, null, contactos);
  }
}

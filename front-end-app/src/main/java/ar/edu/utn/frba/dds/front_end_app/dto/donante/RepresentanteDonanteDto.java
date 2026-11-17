package ar.edu.utn.frba.dds.front_end_app.dto.donante;

import ar.edu.utn.frba.dds.front_end_app.dto.MedioContactoDto;
import ar.edu.utn.frba.dds.front_end_app.dto.TipoDeContactoDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepresentanteDonanteDto(
    Long id,
    String nombre,
    MedioContactoDto contacto,
    List<MedioContactoDto> contactos
) {
  public String getNombrePila() {
    if (nombre == null || nombre.isBlank()) return "";
    String[] partes = nombre.trim().split("\\s+", 2);
    return partes[0];
  }

  public String getApellido() {
    if (nombre == null || nombre.isBlank()) return "";
    String[] partes = nombre.trim().split("\\s+", 2);
    return partes.length > 1 ? partes[1] : "";
  }

  public String getIniciales() {
    if (nombre == null || nombre.isBlank()) return "RP";
    String[] partes = nombre.trim().split("\\s+");
    if (partes.length >= 2 && !partes[0].isEmpty() && !partes[partes.length - 1].isEmpty()) {
      return ("" + partes[0].charAt(0) + partes[partes.length - 1].charAt(0)).toUpperCase();
    }
    return nombre.trim().substring(0, Math.min(2, nombre.trim().length())).toUpperCase();
  }

  public String getEmail() {
    List<MedioContactoDto> lista = obtenerListaContactos();
    return lista.stream()
        .filter(c -> c != null && c.tipoDeContactoDto() == TipoDeContactoDto.EMAIL && c.valorDelContacto() != null && !c.valorDelContacto().isBlank())
        .map(MedioContactoDto::valorDelContacto)
        .findFirst()
        .orElse("");
  }

  public String getTelefono() {
    List<MedioContactoDto> lista = obtenerListaContactos();
    return lista.stream()
        .filter(c -> c != null && (c.tipoDeContactoDto() == TipoDeContactoDto.TELEFONO
            || c.tipoDeContactoDto() == TipoDeContactoDto.SMS
            || c.tipoDeContactoDto() == TipoDeContactoDto.WHATSAPP)
            && c.valorDelContacto() != null && !c.valorDelContacto().isBlank())
        .map(MedioContactoDto::valorDelContacto)
        .findFirst()
        .orElse("");
  }

  public String getResumenContactos() {
    String email = getEmail();
    String tel = getTelefono();
    if (!email.isEmpty() && !tel.isEmpty()) {
      return email + " · " + tel;
    } else if (!email.isEmpty()) {
      return email;
    } else if (!tel.isEmpty()) {
      return tel;
    }
    return "Sin datos de contacto";
  }

  private List<MedioContactoDto> obtenerListaContactos() {
    if (contactos != null && !contactos.isEmpty()) {
      return contactos;
    }
    if (contacto != null) {
      return List.of(contacto);
    }
    return List.of();
  }
}

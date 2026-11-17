package ar.edu.utn.frba.dds.front_end_app.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

public record RegistroDonanteForm(
    String nombre,
    String apellido,
    String numeroDeDocumento,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fechaDeNacimiento,
    String genero,
    String calle,
    Integer altura,
    String username,
    String password,
    String confirmPassword,
    List<String> contactoTipo,
    List<String> contactoValor,
    List<Integer> contactoPreferido
) {
}

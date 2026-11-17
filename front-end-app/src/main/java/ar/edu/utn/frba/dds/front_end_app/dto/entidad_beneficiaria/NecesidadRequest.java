package ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NecesidadRequest{
  Long subcategoriaId;
  Double cantidad;
  String descripcion;
  String tipoNecesidad; // RECURRENTE / EXTRAORDINARIA
  String periodo;// ISO-8601 duration (ej: P1M)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  LocalDate inicioRecurrencia;
}

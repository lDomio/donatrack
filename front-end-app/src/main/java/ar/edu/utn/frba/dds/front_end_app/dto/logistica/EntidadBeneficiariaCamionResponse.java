package ar.edu.utn.frba.dds.front_end_app.dto.logistica;

import ar.edu.utn.frba.dds.front_end_app.dto.DireccionDto;
import ar.edu.utn.frba.dds.front_end_app.dto.NecesidadResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EntidadBeneficiariaCamionResponse {
  Long id;
  String razonSocial;
  String tipo;
  DireccionDto direccion;
}

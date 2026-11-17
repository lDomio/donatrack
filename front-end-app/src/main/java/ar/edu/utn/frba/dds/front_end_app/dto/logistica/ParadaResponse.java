package ar.edu.utn.frba.dds.front_end_app.dto.logistica;

import ar.edu.utn.frba.dds.front_end_app.dto.EntidadBeneficiariaResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParadaResponse {
    private Long id;
    private Integer numeroDeParada;
    private LocalDateTime fechaEntrega;
    private String nombreEntidad;
    private EntregaResponse entrega;
}

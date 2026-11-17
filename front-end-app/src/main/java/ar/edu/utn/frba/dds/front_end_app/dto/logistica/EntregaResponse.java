package ar.edu.utn.frba.dds.front_end_app.dto.logistica;

import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntregaResponse {
    private Long id;
    private String direccionEntidad;
    private Double latitud;
    private Double longitud;
    private Boolean fueRecibidoPorLaEntidad;
    private String estadoActual;
    private List<Long> donacionesIds;
    private List<DonacionResponse> donaciones;
}

package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.DonacionResponse;
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
}

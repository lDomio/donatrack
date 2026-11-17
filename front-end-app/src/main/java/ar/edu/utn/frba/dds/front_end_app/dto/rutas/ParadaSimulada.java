package ar.edu.utn.frba.dds.front_end_app.dto.rutas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParadaSimulada {
    private Long idParada;
    private Long idEntrega;
    private Integer numeroParada;
    private String nombreDestino;
    private double latitud;
    private double longitud;
    private int geometryIndex;
    private List<Long> donacionesIds;
    private boolean completada;
    private boolean cancelada;
}

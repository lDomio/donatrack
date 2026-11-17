package ar.edu.utn.frba.dds.front_end_app.dto.logistica;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RutaResponse {
    private Long id;
    private Double distanciaARecorrer;
    private LocalDateTime fechaDeComienzoDeViaje;
    private LocalDateTime fechaFinDeViaje;
    private String estadoActual;
    private List<ParadaResponse> paradas;
}

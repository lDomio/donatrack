package ar.edu.utn.frba.dds.front_end_app.dto.rutas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDto {
    private Long rutaId;
    private double distanciaKm;
    private double duracionMin;
    private List<double[]> geometry;
    private String status;
    private String mensaje;
}

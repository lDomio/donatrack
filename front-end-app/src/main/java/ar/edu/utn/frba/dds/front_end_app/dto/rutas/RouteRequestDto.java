package ar.edu.utn.frba.dds.front_end_app.dto.rutas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {
    private double origenLat;
    private double origenLng;
    private double destinoLat;
    private double destinoLng;
}

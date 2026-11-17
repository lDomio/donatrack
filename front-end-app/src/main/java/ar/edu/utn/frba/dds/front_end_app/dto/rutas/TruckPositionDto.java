package ar.edu.utn.frba.dds.front_end_app.dto.rutas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TruckPositionDto {
    private Long camionId;
    private Long rutaId;
    private double latitud;
    private double longitud;
    private double porcentaje;
    private double velocidadKmH;
    private double distanciaRestanteKm;
    private double tiempoRestanteMin;
    private String estado;
    private String timestamp;

    // Campos de parada para control y visualización en tiempo real
    private Long paradaActualId;
    private Integer numeroParadaActual;
    private String nombreParadaActual;
    private boolean enParada;

    // Constructor con los 10 campos originales para compatibilidad
    public TruckPositionDto(Long camionId, Long rutaId, double latitud, double longitud,
                            double porcentaje, double velocidadKmH, double distanciaRestanteKm,
                            double tiempoRestanteMin, String estado, String timestamp) {
        this.camionId = camionId;
        this.rutaId = rutaId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.porcentaje = porcentaje;
        this.velocidadKmH = velocidadKmH;
        this.distanciaRestanteKm = distanciaRestanteKm;
        this.tiempoRestanteMin = tiempoRestanteMin;
        this.estado = estado;
        this.timestamp = timestamp;
    }
}

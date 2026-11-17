package ar.edu.utn.frba.dds.logistica.main.dtos.camion;

import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.RutaResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamionResponse {
    private String id;
    private String patente;
    private Double capacidadEnKg;
    private Double volumen;
    private Double altura;
    private Boolean estaEnDeposito;
    private String nombreChofer;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Long idUsuario;
    private List<Long> rutasIds;
    private RutaResponse rutaActiva;
}

package ar.edu.utn.frba.dds.logistica.main.dtos.camion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamionRequest {
    private String patente;
    private Double capacidadEnKg;
    private Double volumen;
    private Double altura;
    private String nombreChofer;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Long idUsuario;

    public CamionRequest(String patente, Double capacidadEnKg, Double volumen, Double altura, String nombreChofer, String direccion, Double latitud, Double longitud) {
        this(patente, capacidadEnKg, volumen, altura, nombreChofer, direccion, latitud, longitud, null);
    }
}

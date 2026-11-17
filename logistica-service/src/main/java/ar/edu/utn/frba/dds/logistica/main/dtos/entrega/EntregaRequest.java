package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntregaRequest {
    private String razonSocialEntidad;
    private String direccionEntidad;
    private Double latitud;
    private Double longitud;
    private List<DonacionEntregaDTO> donaciones;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonacionEntregaDTO {
        private Long id;
        private String subcategoria;
        private Double pesoKg;
        private Double volumen;
    }
}

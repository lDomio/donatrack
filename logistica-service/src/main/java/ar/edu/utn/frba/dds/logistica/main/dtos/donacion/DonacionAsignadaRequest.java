package ar.edu.utn.frba.dds.logistica.main.dtos.donacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonacionAsignadaRequest {
    private Long donacionId;
    private Long donanteId;
    private String subcategoria;
    private Double cantidad;
    private LocalDate fechaVencimiento;
    
    // Datos de la Entidad (Destino)
    private Long entidadId;
    private String razonSocialEntidad;
    private String direccionEntidad;
    private Double latitudDestino;
    private Double longitudDestino;
}

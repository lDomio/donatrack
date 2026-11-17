package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProgresoMision {
    @Column(name = "progreso_mision_actual")
    private Double progreso;

    @Column(name = "cantidad_mision_actual")
    private Double cantidadActual;

    @Column(name = "cantidad_objetivo_mision_actual")
    private Double cantidadObjetivo;
}

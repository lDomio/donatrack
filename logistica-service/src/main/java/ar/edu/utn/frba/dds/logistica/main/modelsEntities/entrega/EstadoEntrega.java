package ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import dev.morphia.annotations.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EstadoEntrega {
    private String observaciones;
    private LocalDate fecha;
    private TipoDeEstadoEntrega tipoEstadoEntrega;
}

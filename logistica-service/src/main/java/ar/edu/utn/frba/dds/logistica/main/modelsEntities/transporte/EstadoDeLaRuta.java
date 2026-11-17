package ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte;

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
public class EstadoDeLaRuta {
    private LocalDate fecha;
    private TipoDeEstadoRuta tipoDeEstadoRuta;
}

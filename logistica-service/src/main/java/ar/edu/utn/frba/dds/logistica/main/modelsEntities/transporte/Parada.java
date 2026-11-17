package ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import lombok.*;

import java.time.LocalDateTime;

import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Parada {
    private Long id;
    private Integer numeroDeParada;
    private LocalDateTime fechaEntrega;

    @Reference(lazy = true, ignoreMissing = true)
    private Entrega entrega;
}

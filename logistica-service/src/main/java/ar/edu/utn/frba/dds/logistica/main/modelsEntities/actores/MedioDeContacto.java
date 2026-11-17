package ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import dev.morphia.annotations.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MedioDeContacto {
    private String valor;
    private TipoDeContacto tipo;
}

package ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion;

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
public class Provincia {
    private String nombre;
    private Pais pais;
}

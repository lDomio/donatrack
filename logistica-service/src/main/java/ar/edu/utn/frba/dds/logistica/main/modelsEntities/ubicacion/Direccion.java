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
public class Direccion {
    private String calle;
    private int altura;
    private Ciudad ciudad;
    private Double latitud;
    private Double longitud;
}

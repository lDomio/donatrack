package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Direccion;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("HUMANA")
public class Humana extends Donante {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "fechaDeNacimiento")
    private LocalDate fechaDeNacimiento;
    @Column(name = "genero")
    private String genero;
    @Column(name = "numeroDeDocumento")
    private String numeroDeDocumento;
    @Embedded
    private Direccion direccion;
}

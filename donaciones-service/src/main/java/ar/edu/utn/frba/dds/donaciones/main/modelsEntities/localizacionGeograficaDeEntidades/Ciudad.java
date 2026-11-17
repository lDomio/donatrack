package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;

@Getter
@Embeddable
public class Ciudad {

  @Column(name="nombreCiudad")
  private String Nombre;

  @Embedded
  private Pais pais;
}

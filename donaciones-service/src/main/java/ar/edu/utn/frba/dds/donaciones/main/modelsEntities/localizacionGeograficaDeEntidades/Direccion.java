package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Direccion {

  @Column(name="calle")
  private String calle;

  @Column(name="altura")
  private Integer altura;

  @Embedded
  private Ciudad ciudad;

  @Column(name="latitud")
  private Double latitud;

  @Column(name="longitud")
  private Double longitud;


}

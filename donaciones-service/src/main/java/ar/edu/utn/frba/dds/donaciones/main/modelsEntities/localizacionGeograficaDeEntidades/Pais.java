package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Pais {

  @Column(name="nombrePais")
  String nombre;

  @Column(name="codigoIsoPais")
  String codigoIso;
}

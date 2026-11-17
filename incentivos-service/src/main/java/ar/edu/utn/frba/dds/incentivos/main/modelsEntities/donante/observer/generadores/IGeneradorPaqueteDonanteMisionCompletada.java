package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import java.util.Optional;

public interface IGeneradorPaqueteDonanteMisionCompletada {
  public Optional<Paquete> generarPaqueteSiCorresponde(MisionCompletadaEvento misionCompletadaEvento);
}

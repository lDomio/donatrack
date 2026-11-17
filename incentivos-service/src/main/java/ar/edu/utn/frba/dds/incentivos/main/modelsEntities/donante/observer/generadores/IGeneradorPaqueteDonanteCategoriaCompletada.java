package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import java.util.Optional;

public interface IGeneradorPaqueteDonanteCategoriaCompletada {
  public Optional<Paquete> generarPaqueteSiCorresponde(CategoriaCompletadaEvento categoriaCompletadaEvento);
}

package ar.edu.utn.frba.dds.incentivos.main.services;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import java.util.Optional;

public interface MisionService {
  public Optional<Mision> obtenerPrimeraMision();
}

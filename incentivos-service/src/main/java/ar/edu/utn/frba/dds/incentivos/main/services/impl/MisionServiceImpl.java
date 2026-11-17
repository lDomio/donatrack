package ar.edu.utn.frba.dds.incentivos.main.services.impl;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.repositories.MisionRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.MisionService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MisionServiceImpl implements MisionService {
  private final MisionRepository repository;

  public MisionServiceImpl(MisionRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Mision> obtenerPrimeraMision() {
    return repository.obtenerPrimeraMision();
  }
}

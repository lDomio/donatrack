package ar.edu.utn.frba.dds.donaciones.main.repositories.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.matcheadorDonaciones.MatcheoSugerido;
import ar.edu.utn.frba.dds.donaciones.main.repositories.SugerenciaMatchRepository;
import ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
@Repository
public class SugerenciaMatchRepositoryImpl implements SugerenciaMatchRepository {
  private final List<MatcheoSugerido> sugerenciasMatch = new ArrayList<>();
  private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

  @Override
  public List<MatcheoSugerido> findAll() {
    return new ArrayList<>(sugerenciasMatch);
  }

  @Override
  public void guardar(MatcheoSugerido sugerencia) {
    // 1. Evitamos duplicados por donación
    sugerenciasMatch.removeIf(s -> s.getIdDonacion().equals(sugerencia.getIdDonacion()));

    // 2. Asignamos ID si es necesario
    if (sugerencia.getId() == null || sugerencia.getId() == 0L) {
      sugerencia.setId(generadorId.siguiente());
    }

    // 3. Guardamos
    sugerenciasMatch.add(sugerencia);
  }

  @Override
  public Optional<MatcheoSugerido> findById(Long idMatcheo){
    return sugerenciasMatch.stream().filter(sugerido-> sugerido.getId().equals(idMatcheo)).findFirst();
  }
}
*/
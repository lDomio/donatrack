package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.matcheadorDonaciones.MatcheoSugerido;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SugerenciaMatchRepository extends JpaRepository<MatcheoSugerido, Long> {
  /*public List<MatcheoSugerido> findAll();
  void guardar(MatcheoSugerido sugerencia);
  public Optional<MatcheoSugerido> findById(Long idMatcheo);*/
}

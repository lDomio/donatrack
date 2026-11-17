package ar.edu.utn.frba.dds.incentivos.main.repositories;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MisionRepository extends JpaRepository<Mision, Long> {
  @Query("SELECT m FROM Mision m WHERE m.misionAnterior IS NULL")
  Optional<Mision> obtenerPrimeraMision();
}

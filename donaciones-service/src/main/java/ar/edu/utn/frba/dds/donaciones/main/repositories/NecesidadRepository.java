package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NecesidadRepository extends JpaRepository<Necesidad, Long> {
}

package ar.edu.utn.frba.dds.incentivos.main.repositories;


import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {
  List<Paquete> findByFueEnviadoFalse();
}

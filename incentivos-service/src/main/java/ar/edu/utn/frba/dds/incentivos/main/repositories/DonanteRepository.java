package ar.edu.utn.frba.dds.incentivos.main.repositories;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonanteRepository extends JpaRepository<Donante, Long> {
    Optional<Donante> findByIdDonanteDonacionesService(Long idDonanteDonacionesService);
}

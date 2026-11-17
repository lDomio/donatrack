package ar.edu.utn.frba.dds.incentivos.main.repositories;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    List<Donacion> findAllByDonante_Id(Long donanteId);
    Optional<Donacion> findByIdDonacionDonacionesService(Long idDonacionDonacionesService);
}


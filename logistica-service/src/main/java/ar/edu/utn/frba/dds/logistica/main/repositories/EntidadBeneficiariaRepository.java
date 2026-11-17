package ar.edu.utn.frba.dds.logistica.main.repositories;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import java.util.List;
import java.util.Optional;

public interface EntidadBeneficiariaRepository {
    List<EntidadBeneficiaria> findAll();
    Optional<EntidadBeneficiaria> findById(Long id);
    Optional<EntidadBeneficiaria> findByIdEntidadDonacionesService(Long idEntidadDonacionesService);
    void guardar(EntidadBeneficiaria entidad);
    void deleteAll();
}

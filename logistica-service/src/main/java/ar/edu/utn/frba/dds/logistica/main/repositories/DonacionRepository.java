package ar.edu.utn.frba.dds.logistica.main.repositories;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import java.util.List;
import java.util.Optional;

public interface DonacionRepository {
    List<Donacion> findAll();

    Optional<Donacion> findById(Long id);

    void guardar(Donacion donacion);

    void eliminar(Long id);
    void deleteAll();

}

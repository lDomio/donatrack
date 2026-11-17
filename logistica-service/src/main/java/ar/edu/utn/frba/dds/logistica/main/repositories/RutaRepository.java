package ar.edu.utn.frba.dds.logistica.main.repositories;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import java.util.List;
import java.util.Optional;

public interface RutaRepository {
    Long guardar(Ruta ruta);
    Optional<Ruta> findById(Long id);
    List<Ruta> findAll();
    void eliminar(Long id);
    void deleteAll();
}

package ar.edu.utn.frba.dds.logistica.main.repositories;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import java.util.List;
import java.util.Optional;

public interface EntregaRepository {
    List<Entrega> findAll();
    Optional<Entrega> findById(Long id);
    Optional<Entrega> findByCodigo(String codigo);
    Long guardar(Entrega entrega);
    List<Entrega> findPendientes();
    void deleteAll();
}

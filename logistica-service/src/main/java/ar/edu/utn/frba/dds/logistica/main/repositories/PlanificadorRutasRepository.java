package ar.edu.utn.frba.dds.logistica.main.repositories;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.PlanificadorRutaRequest;

import java.util.List;
import java.util.Optional;

public interface PlanificadorRutasRepository {
    String guardar(PlanificadorRutaRequest planificadorRutaRequest);
    Optional<PlanificadorRutaRequest> findById(String id);
    void eliminar(String id);
    List<PlanificadorRutaRequest> findAll();
    void deleteAll();
}


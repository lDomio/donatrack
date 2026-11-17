package ar.edu.utn.frba.dds.logistica.main.repositories.impl;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.PlanificadorRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.repositories.PlanificadorRutasRepository;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PlanificadorRutasRepositoryImpl implements PlanificadorRutasRepository {
    private final Datastore datastore;

    public PlanificadorRutasRepositoryImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public String guardar(PlanificadorRutaRequest planificadorRutaRequest) {
        if (planificadorRutaRequest.getRequestId() == null) {
            planificadorRutaRequest.setRequestId(UUID.randomUUID().toString());
        }

        datastore.save(planificadorRutaRequest);

        return planificadorRutaRequest.getRequestId();

    }

    @Override
    public Optional<PlanificadorRutaRequest> findById(String requestId) {
        return Optional.ofNullable(
                datastore.find(PlanificadorRutaRequest.class).filter(Filters.eq("requestId", requestId)).first());

    }

    @Override
    public void eliminar(String requestId) {
        datastore.find(PlanificadorRutaRequest.class).filter(Filters.eq("requestId", requestId)).delete();
    }

    @Override
    public List<PlanificadorRutaRequest> findAll() {
        return datastore.find(PlanificadorRutaRequest.class).iterator().toList();
    }
    @Override
    public void deleteAll() {
        datastore.find(PlanificadorRutaRequest.class).delete();
    }
}

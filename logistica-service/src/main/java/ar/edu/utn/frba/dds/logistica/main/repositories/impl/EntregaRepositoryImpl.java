package ar.edu.utn.frba.dds.logistica.main.repositories.impl;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.TipoDeEstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntregaRepository;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EntregaRepositoryImpl implements EntregaRepository {
    private final Datastore datastore;

    public EntregaRepositoryImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public List<Entrega> findAll() {
        return datastore.find(Entrega.class).iterator().toList();
    }

    @Override
    public Optional<Entrega> findById(Long id) {
        return Optional.ofNullable(datastore.find(Entrega.class).filter(Filters.eq("id", id)).first());
    }

    @Override
    public Optional<Entrega> findByCodigo(String codigo) {
        if (codigo == null || !codigo.contains("-")) {
            return Optional.empty();
        }
        try {
            String idString = codigo.split("-")[1];
            Long id = Long.valueOf(idString);
            return this.findById(id);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Long guardar(Entrega entrega) {
        if (entrega.getId() == null) {
            long maxId = findAll().stream()
                    .mapToLong(e -> e.getId() != null ? e.getId() : 0L)
                    .max()
                    .orElse(0L);
            entrega.setId(maxId + 1);
        }
        datastore.save(entrega);
        return entrega.getId();
    }

    @Override
    public List<Entrega> findPendientes() {
        return findAll().stream()
                .filter(e -> !e.getEstadosEntrega().isEmpty() &&
                        e.getEstadosEntrega().getLast().getTipoEstadoEntrega() == TipoDeEstadoEntrega.PENDIENTE)
                .collect(Collectors.toList());
    }
    @Override
    public void deleteAll() {
        datastore.find(Entrega.class).delete();
    }
}

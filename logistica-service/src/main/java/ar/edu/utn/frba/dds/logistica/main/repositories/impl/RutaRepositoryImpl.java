package ar.edu.utn.frba.dds.logistica.main.repositories.impl;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import ar.edu.utn.frba.dds.logistica.main.repositories.RutaRepository;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RutaRepositoryImpl implements RutaRepository {
    private final Datastore datastore;

    public RutaRepositoryImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public synchronized Long guardar(Ruta ruta) {
        if (ruta.getId() == null) {
            long maxId = findAll().stream()
                    .mapToLong(r -> r.getId() != null ? r.getId() : 0L)
                    .max()
                    .orElse(0L);
            ruta.setId(maxId + 1);
        }
        datastore.save(ruta);
        return ruta.getId();
    }

    @Override
    public Optional<Ruta> findById(Long id) {
        return Optional.ofNullable(datastore.find(Ruta.class).filter(Filters.eq("id", id)).first());
    }

    @Override
    public List<Ruta> findAll() {
        return datastore.find(Ruta.class).iterator().toList();
    }

    @Override
    public void eliminar(Long id) {
        datastore.find(Ruta.class).filter(Filters.eq("id", id)).delete();
    }
    @Override
    public void deleteAll() {
        datastore.find(Ruta.class).delete();
    }
}

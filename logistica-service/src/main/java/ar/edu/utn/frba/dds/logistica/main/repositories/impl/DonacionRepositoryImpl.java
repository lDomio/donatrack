package ar.edu.utn.frba.dds.logistica.main.repositories.impl;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.repositories.DonacionRepository;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class DonacionRepositoryImpl implements DonacionRepository {
    private final Datastore datastore;

    public DonacionRepositoryImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public List<Donacion> findAll() {
        return datastore.find(Donacion.class).iterator().toList();
    }

    @Override
    public Optional<Donacion> findById(Long id) {
        return Optional.ofNullable(datastore.find(Donacion.class).filter(Filters.eq("id", id)).first());

    }

    @Override
    public void guardar(Donacion donacion) {
        datastore.save(donacion);
    }

    @Override
    public void eliminar(Long id) {
        datastore.find(Donacion.class).filter(Filters.eq("id", id)).delete();
    }
    @Override
    public void deleteAll() {
        datastore.find(Donacion.class).delete();
    }
}

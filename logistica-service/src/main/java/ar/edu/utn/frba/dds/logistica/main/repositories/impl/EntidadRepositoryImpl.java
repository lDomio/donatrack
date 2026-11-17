package ar.edu.utn.frba.dds.logistica.main.repositories.impl;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntidadBeneficiariaRepository;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EntidadRepositoryImpl implements EntidadBeneficiariaRepository {
    private final Datastore datastore;

    public EntidadRepositoryImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public List<EntidadBeneficiaria> findAll() {
        return datastore.find(EntidadBeneficiaria.class).iterator().toList();
    }

    @Override
    public Optional<EntidadBeneficiaria> findById(Long id) {
        return Optional.ofNullable(datastore.find(EntidadBeneficiaria.class).filter(Filters.eq("id", id)).first());
    }

    @Override
    public Optional<EntidadBeneficiaria> findByIdEntidadDonacionesService(Long idEntidadDonacionesService) {
        return Optional.ofNullable(
                datastore.find(EntidadBeneficiaria.class)
                        .filter(Filters.eq("idEntidadDonacionesService", idEntidadDonacionesService))
                        .first()
        );
    }

    @Override
    public void guardar(EntidadBeneficiaria entidad) {
        datastore.save(entidad);
    }

    @Override
    public void deleteAll() {
        datastore.find(EntidadBeneficiaria.class).delete();
    }
}

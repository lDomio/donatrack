package ar.edu.utn.frba.dds.logistica.main.repositories;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface CamionRepository {
    List<Camion> findAll();
    Optional<Camion> findById(ObjectId id);
    Optional<Camion> findById(String id);
    Optional<Camion> findByIdUsuario(Long idUsuario);
    String guardar(Camion camion);
    void eliminar(ObjectId id);
    void eliminar(String id);
    void deleteAll();
}

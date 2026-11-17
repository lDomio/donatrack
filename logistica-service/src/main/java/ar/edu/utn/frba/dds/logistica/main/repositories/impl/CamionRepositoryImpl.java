package ar.edu.utn.frba.dds.logistica.main.repositories.impl;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.repositories.CamionRepository;
import com.mongodb.client.MongoCollection;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CamionRepositoryImpl implements CamionRepository {
    private final Datastore datastore;

    public CamionRepositoryImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    private synchronized void migrarColeccionSiEsNecesario() {
        try {
            if (datastore == null || datastore.getDatabase() == null) {
                return;
            }
            MongoCollection<Document> collection = datastore.getDatabase().getCollection("camiones");
            Document primerDoc = collection.find().first();
            if (primerDoc != null && !(primerDoc.get("_id") instanceof ObjectId)) {
                System.out.println("Detectados camiones con _id numérico heredado. Migrando colección 'camiones' a ObjectId...");
                List<Document> documentosViejos = collection.find().into(new ArrayList<>());
                collection.drop();
                for (Document doc : documentosViejos) {
                    doc.put("_id", new ObjectId());
                    collection.insertOne(doc);
                }
                System.out.println("Migración de 'camiones' a ObjectId completada exitosamente.");
            }
        } catch (Exception e) {
            System.err.println("Aviso al verificar/migrar colección de camiones: " + e.getMessage());
        }
    }

    @Override
    public List<Camion> findAll() {
        migrarColeccionSiEsNecesario();
        return datastore.find(Camion.class).iterator().toList();
    }

    @Override
    public Optional<Camion> findById(ObjectId id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(datastore.find(Camion.class).filter(Filters.eq("_id", id)).first());
    }

    @Override
    public Optional<Camion> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        migrarColeccionSiEsNecesario();
        if (ObjectId.isValid(id)) {
            return findById(new ObjectId(id));
        }
        // Fallback por patente si por alguna razón no es un hex ObjectId válido
        return Optional.ofNullable(datastore.find(Camion.class).filter(Filters.eq("patente", id)).first());
    }

    @Override
    public Optional<Camion> findByIdUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(datastore.find(Camion.class).filter(Filters.eq("idUsuario", idUsuario)).first());
    }

    @Override
    public String guardar(Camion camion) {
        datastore.save(camion);
        return camion.getId() != null ? camion.getId().toHexString() : null;
    }

    @Override
    public void eliminar(ObjectId id) {
        if (id != null) {
            datastore.find(Camion.class).filter(Filters.eq("_id", id)).delete();
        }
    }

    @Override
    public void eliminar(String id) {
        if (id != null && ObjectId.isValid(id)) {
            eliminar(new ObjectId(id));
        }
    }
    @Override
    public void deleteAll() {
        datastore.find(Camion.class).delete();
    }
}

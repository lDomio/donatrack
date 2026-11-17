package ar.edu.utn.frba.dds.notificacion.main.repositories;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}


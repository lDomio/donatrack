package ar.edu.utn.frba.dds.notificacion.main.repositories;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.Destinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.TipoDestinatario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinatarioRepository extends JpaRepository<Destinatario, Long> {
    Optional<Destinatario> findByIdDestinatarioOrigenAndTipoDestinatario(Long idDestinatarioOrigen, TipoDestinatario tipoDestinatario);
    Optional<Destinatario> findByIdDestinatarioOrigen(Long idDestinatarioOrigen);
}

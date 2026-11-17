package ar.edu.utn.frba.dds.autenticacion.repositories;

import ar.edu.utn.frba.dds.autenticacion.modelEntities.Rol;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
  Optional<Rol> findByTipoRol(TipoRol tipoRol);
}

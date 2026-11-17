package ar.edu.utn.frba.dds.autenticacion.repositories;

import ar.edu.utn.frba.dds.autenticacion.modelEntities.Permisos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermisosRepository extends JpaRepository<Permisos, Long> {
  Optional<Permisos> findByPermiso(String permiso);
}

package ar.edu.utn.frba.dds.autenticacion.repositories;

import ar.edu.utn.frba.dds.autenticacion.modelEntities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByNombreDeUsuario(String nombreDeUsuario);

  default Usuario guardar(Usuario usuario) {
    return save(usuario);
  }
}

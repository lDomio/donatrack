package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonanteRepository extends JpaRepository<Donante, Long> {
  Optional<Donante> findByIdUsuarioAuth(Long idUsuarioAuth);

  @Query("SELECT d FROM Donante d WHERE d.estaActivo = true")
  List<Donante> findAllActivos();

  @Query("SELECT d FROM Donante d WHERE d.id = :id AND d.estaActivo = true")
  Optional<Donante> findByIdActivo(@Param("id") Long id);

  @Query("SELECT d FROM Donante d WHERE d.idUsuarioAuth = :idUsuarioAuth AND d.estaActivo = true")
  Optional<Donante> findByIdUsuarioAuthActivo(@Param("idUsuarioAuth") Long idUsuarioAuth);
}


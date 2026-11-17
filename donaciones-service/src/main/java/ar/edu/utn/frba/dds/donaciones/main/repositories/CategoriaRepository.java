package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}

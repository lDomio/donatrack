package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {
    Optional<Subcategoria> findByNombre(String nombre);
    List<Subcategoria> findByCategoria_Id(Long categoriaId);
}

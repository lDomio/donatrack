package ar.edu.utn.frba.dds.incentivos.main.repositories;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaDonanteRepository extends JpaRepository<CategoriaDonante, Long> {
}

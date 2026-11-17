package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EntidadBeneficiariaRepository extends JpaRepository<EntidadBeneficiaria, Long> {
    Optional<EntidadBeneficiaria> findByRazonSocial(String razonSocial);
    Optional<EntidadBeneficiaria> findByIdUsuarioAuth(Long idUsuario);

    /**
     * Filtros opcionales (null = no filtra). `busqueda` matchea razonSocial O
     * calle O ciudad; `nombre`/`ubicacion` son filtros finos en AND; `tipo` es
     * el codigo canonico con comparacion case-insensitive. Los textos vienen
     * con los comodines LIKE escapados desde el service, por eso cada LIKE
     * declara su ESCAPE.
     */
    @Query("""
        SELECT e FROM EntidadBeneficiaria e
        WHERE (:busqueda IS NULL OR LOWER(e.razonSocial) LIKE LOWER(CONCAT('%', :busqueda, '%')) ESCAPE '\\'
            OR LOWER(e.direccion.calle) LIKE LOWER(CONCAT('%', :busqueda, '%')) ESCAPE '\\'
            OR LOWER(e.direccion.ciudad.Nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) ESCAPE '\\')
          AND (:nombre IS NULL OR LOWER(e.razonSocial) LIKE LOWER(CONCAT('%', :nombre, '%')) ESCAPE '\\')
          AND (:ubicacion IS NULL OR LOWER(e.direccion.calle) LIKE LOWER(CONCAT('%', :ubicacion, '%')) ESCAPE '\\'
            OR LOWER(e.direccion.ciudad.Nombre) LIKE LOWER(CONCAT('%', :ubicacion, '%')) ESCAPE '\\')
          AND (:tipo IS NULL OR LOWER(e.tipo.tipoEntidad) = LOWER(:tipo))
        """)
    List<EntidadBeneficiaria> buscarConFiltros(@Param("busqueda") String busqueda,
                                              @Param("nombre") String nombre,
                                              @Param("ubicacion") String ubicacion,
                                              @Param("tipo") String tipo);
}

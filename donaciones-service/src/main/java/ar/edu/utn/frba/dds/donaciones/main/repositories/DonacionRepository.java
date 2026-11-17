package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    public List<Donacion> findByIdDonante(Long idDonante);
    public List<Donacion> getByNecesidad_id(Long idNecesidad);

    /**
     * Cuenta las entidades beneficiarias distintas ayudadas por un donante en un
     * rango de fechas. Solo cuentan donaciones con necesidad asignada
     * (el JOIN interno excluye las que tienen necesidad_id NULL, es decir,
     * las que siguen en deposito sin matchear) y la fecha que manda es
     * fechaDeCreacion, no la de los estados.
     */
    @Query("SELECT COUNT(DISTINCT n.entidadBeneficiaria.id) FROM Donacion d JOIN d.necesidad n WHERE d.idDonante = :idDonante AND d.fechaDeCreacion BETWEEN :inicio AND :fin")
    long contarEntidadesDistintasAyudadas(@Param("idDonante") Long idDonante, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}

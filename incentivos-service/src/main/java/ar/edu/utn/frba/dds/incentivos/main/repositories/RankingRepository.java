package ar.edu.utn.frba.dds.incentivos.main.repositories;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<RankingDonantes, Long> {
    Optional<RankingDonantes> findFirstByOrderByFechaRankingDesc();

    @Query("SELECT r FROM RankingDonantes r WHERE YEAR(r.fechaRanking) = :anio AND MONTH(r.fechaRanking) = :mes ORDER BY r.fechaRanking DESC")
    List<RankingDonantes> findByAnioAndMes(@Param("anio") int anio, @Param("mes") int mes);
}

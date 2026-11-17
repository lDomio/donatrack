/*package ar.edu.utn.frba.dds.incentivos.main.repositories.impl;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;

import ar.edu.utn.frba.dds.incentivos.main.repositories.RankingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RankingRepositoryImpl implements RankingRepository {
    private final List<RankingDonantes> rankings = new ArrayList<>();

    @Override
    public void guardar(RankingDonantes ranking) {
        rankings.removeIf(r -> r.getPeriodo() != null && r.getPeriodo().equals(ranking.getPeriodo()));
        rankings.add(ranking);
    }

    @Override
    public Optional<RankingDonantes> findUltimoRanking() {
        if (this.rankings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.rankings.getLast());
    }
}
*/
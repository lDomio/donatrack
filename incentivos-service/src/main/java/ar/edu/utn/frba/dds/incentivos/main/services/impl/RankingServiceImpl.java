package ar.edu.utn.frba.dds.incentivos.main.services.impl;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;
import ar.edu.utn.frba.dds.incentivos.main.repositories.RankingRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import ar.edu.utn.frba.dds.incentivos.main.services.RankingService;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RankingServiceImpl implements RankingService {
    private static final String ZONA = "America/Argentina/Buenos_Aires";

    private final RankingRepository rankingRepository;
    private final DonanteService donanteService;

    public RankingServiceImpl(RankingRepository rankingRepository, DonanteService donanteService) {
        this.rankingRepository = rankingRepository;
        this.donanteService = donanteService;
    }

    @Override
    @Transactional
    public RankingDonantes generarRanking(YearMonth periodo) {
        List<RankingDonantes> existentes =
                rankingRepository.findByAnioAndMes(periodo.getYear(), periodo.getMonthValue());
        existentes.forEach(rankingRepository::delete);

        List<Donante> donantes = donanteService.findAll();
        RankingDonantes ranking = new RankingDonantes(periodo.atDay(1), donantes);
        return rankingRepository.save(ranking);
    }

    @Override
    @Transactional
    public RankingDonantes generarRanking() {
        return generarRanking(YearMonth.now(ZoneId.of(ZONA)));
    }

    @Override
    @Transactional(readOnly = true)
    public Number buscarPosicionDonanteEnRanking(Long id) {
        RankingDonantes ranking = rankingRepository.findFirstByOrderByFechaRankingDesc()
                .orElse(null);
        return ranking != null ? ranking.obtenerPosicion(id) : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public RankingDonantes obtenerUltimoRanking(Integer top) {
        return rankingRepository.findFirstByOrderByFechaRankingDesc()
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public RankingDonantes obtenerRankingPorPeriodo(Integer mes, Integer anio) {
        if (mes == null && anio == null) {
            return rankingRepository.findFirstByOrderByFechaRankingDesc().orElse(null);
        }

        LocalDate hoy = LocalDate.now(ZoneId.of(ZONA));
        int mesFinal = (mes != null && mes >= 1 && mes <= 12) ? mes : hoy.getMonthValue();
        int anioFinal = (anio != null && anio >= 2000) ? anio : hoy.getYear();

        List<RankingDonantes> rankings = rankingRepository.findByAnioAndMes(anioFinal, mesFinal);
        if (!rankings.isEmpty()) {
            return rankings.get(0);
        }

        return null;
    }
}

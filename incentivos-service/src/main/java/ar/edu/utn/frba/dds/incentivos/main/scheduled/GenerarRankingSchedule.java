package ar.edu.utn.frba.dds.incentivos.main.scheduled;

import ar.edu.utn.frba.dds.incentivos.main.services.RankingService;
import java.time.YearMonth;
import java.time.ZoneId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GenerarRankingSchedule {
  private static final String ZONA = "America/Argentina/Buenos_Aires";

  private final RankingService rankingService;

  public GenerarRankingSchedule(RankingService rankingService) {
    this.rankingService = rankingService;
  }

  @Scheduled(cron = "0 0 3 1 * ?", zone = ZONA)
  public void generarRankingMensual() {
    YearMonth periodo = YearMonth.now(ZoneId.of(ZONA)).minusMonths(1);
    this.rankingService.generarRanking(periodo);
  }
}

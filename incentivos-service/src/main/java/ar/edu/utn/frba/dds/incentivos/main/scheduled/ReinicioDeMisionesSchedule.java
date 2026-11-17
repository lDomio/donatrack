package ar.edu.utn.frba.dds.incentivos.main.scheduled;

import ar.edu.utn.frba.dds.incentivos.main.services.IncentivoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReinicioDeMisionesSchedule {
  private final IncentivoService incentivoService;

  public ReinicioDeMisionesSchedule(IncentivoService incentivoService) {
    this.incentivoService = incentivoService;
  }

  @Scheduled(cron = "0 0 3 1 * ?", zone = "America/Argentina/Buenos_Aires") // 03:00 del día 1, hora Argentina
  public void reiniciarMisionesMensuales(){
    incentivoService.reiniciarMisionesMensuales();
  }
}

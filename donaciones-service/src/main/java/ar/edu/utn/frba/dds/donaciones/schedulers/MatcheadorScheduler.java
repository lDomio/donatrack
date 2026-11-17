package ar.edu.utn.frba.dds.donaciones.schedulers;

import ar.edu.utn.frba.dds.donaciones.main.services.DonacionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MatcheadorScheduler {
  private final DonacionService donacionService;

  public MatcheadorScheduler(DonacionService donacionService) {
    this.donacionService = donacionService;
  }

  //@Scheduled (cron = "0 0 3 * * *")
  @Scheduled (cron = "0 * * * * *")
  public void evaluarMatcheo(){
    donacionService.realizarMatcheoDonaciones();
  }
}

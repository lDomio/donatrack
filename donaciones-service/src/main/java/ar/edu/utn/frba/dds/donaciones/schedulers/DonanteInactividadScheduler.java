package ar.edu.utn.frba.dds.donaciones.schedulers;

import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DonanteInactividadScheduler {

    private final DonanteService donanteService;

    public DonanteInactividadScheduler(DonanteService donanteService) {
        this.donanteService = donanteService;
    }

    // Se ejecuta todos los días a las 10:00 AM
    @Scheduled(fixedRate = 1728000000L)
    public void verificarInactividadDonantes() {
        donanteService.verificarInactividadDonantes();
    }
}


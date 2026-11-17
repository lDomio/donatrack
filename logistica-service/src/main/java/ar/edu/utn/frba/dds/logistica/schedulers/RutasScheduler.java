package ar.edu.utn.frba.dds.logistica.schedulers;

import ar.edu.utn.frba.dds.logistica.main.services.PlanificadorDeRutasService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RutasScheduler {
    private final PlanificadorDeRutasService planificadorDeRutasService;

    public RutasScheduler(PlanificadorDeRutasService planificadorDeRutasService){
        this.planificadorDeRutasService = planificadorDeRutasService;
    }

    @Scheduled(initialDelay = 30000, fixedDelay = 30000)
    public void generarRutas(){
        System.out.println("generando rutas");
        planificadorDeRutasService.generarRutas();
    }

    @Scheduled(initialDelay = 30000, fixedDelay = 30000)
    public void tratarPedidosNoRecibidos(){
        System.out.println("Tratando no recibidos");
        planificadorDeRutasService.tratarPedidosNoRecibidos();
    }
}

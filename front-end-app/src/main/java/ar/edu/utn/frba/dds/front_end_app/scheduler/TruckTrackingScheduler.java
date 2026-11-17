package ar.edu.utn.frba.dds.front_end_app.scheduler;

import ar.edu.utn.frba.dds.front_end_app.dto.rutas.TruckPositionDto;
import ar.edu.utn.frba.dds.front_end_app.services.rutas.SimulationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TruckTrackingScheduler {

    private final SimpMessagingTemplate messagingTemplate;
    private final SimulationService simulationService;

    public TruckTrackingScheduler(SimpMessagingTemplate messagingTemplate, SimulationService simulationService) {
        this.messagingTemplate = messagingTemplate;
        this.simulationService = simulationService;
    }

    @Scheduled(fixedRate = 1000)
    public void emitirPosicionCamion() {
        if (!simulationService.hasActiveSimulation()) {
            return;
        }

        TruckPositionDto posicion = simulationService.getNextPosition();
        if (posicion != null) {
            messagingTemplate.convertAndSend("/topic/posicion-camion", posicion);
        }
    }
}

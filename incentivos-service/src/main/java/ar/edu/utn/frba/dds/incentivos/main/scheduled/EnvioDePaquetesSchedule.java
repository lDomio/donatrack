package ar.edu.utn.frba.dds.incentivos.main.scheduled;

import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import ar.edu.utn.frba.dds.incentivos.main.services.PaqueteService;
import ar.edu.utn.frba.dds.incentivos.main.services.impl.DonanteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EnvioDePaquetesSchedule {

  @Autowired
  private PaqueteService paqueteService;
  
  @Scheduled(fixedDelay = 60000)
  public void enviarPedidos(){
    paqueteService.enviarPaquetes();
  }

}

package ar.edu.utn.frba.dds.incentivos.main.services.impl;

import ar.edu.utn.frba.dds.incentivos.main.dtos.InsigniaObtenidaRequest;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.generadoresMisionCompletada.GeneradorPaqueteDonanteMisionCompletadaN8N;
import ar.edu.utn.frba.dds.incentivos.main.services.DifusionService;
import ar.edu.utn.frba.dds.incentivos.main.services.PaqueteService;
import org.springframework.stereotype.Service;

@Service
public class DifusionServiceImpl implements DifusionService {
  private final PaqueteService paqueteService;
  private final GeneradorPaqueteDonanteMisionCompletadaN8N generadorPaqueteDonanteMisionCompletadaN8N;

  public DifusionServiceImpl(PaqueteService paqueteService, GeneradorPaqueteDonanteMisionCompletadaN8N generadorPaqueteDonanteMisionCompletadaN8N) {
    this.paqueteService = paqueteService;
    this.generadorPaqueteDonanteMisionCompletadaN8N = generadorPaqueteDonanteMisionCompletadaN8N;
  }

  // Ciclo de vida del mensaje de obtencion de insignia
  public void difundirMensajeObtencionInsignia(InsigniaObtenidaRequest insigniaObtenida) {
    paqueteService.guardarPaquete(generadorPaqueteDonanteMisionCompletadaN8N.generarPaqueteParaService(insigniaObtenida.nombreUsuario(),insigniaObtenida.nombreInsignia(),insigniaObtenida.tipoMision()));
  }
}

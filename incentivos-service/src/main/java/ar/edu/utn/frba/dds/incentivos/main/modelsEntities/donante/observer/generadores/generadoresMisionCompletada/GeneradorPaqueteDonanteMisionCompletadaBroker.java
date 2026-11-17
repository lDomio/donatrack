package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.generadoresMisionCompletada;

import ar.edu.utn.frba.dds.common.messaging.EventsTypeEnum;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteMisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.MisionCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteBroker.PaqueteNotificacionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GeneradorPaqueteDonanteMisionCompletadaBroker implements IGeneradorPaqueteDonanteMisionCompletada {
  private final PaqueteNotificacionFactory paqueteNotificacionFactory;

  public GeneradorPaqueteDonanteMisionCompletadaBroker(PaqueteNotificacionFactory paqueteNotificacionFactory) {
    this.paqueteNotificacionFactory = paqueteNotificacionFactory;
  }

  @Override
  public Optional<Paquete> generarPaqueteSiCorresponde(MisionCompletadaEvento misionCompletadaEvento) {
    Map<String, String> extraData = new HashMap<>();
    if (misionCompletadaEvento.insigniaOtorgada() != null) {
      extraData.put("insignia", misionCompletadaEvento.insigniaOtorgada().getDescripcion());
    }
    if (misionCompletadaEvento.misionAnterior() != null && misionCompletadaEvento.misionAnterior().getMision() != null) {
      extraData.put("mision", misionCompletadaEvento.misionAnterior().getMision().getNombre());
    }
    return paqueteNotificacionFactory.crear(EventsTypeEnum.MISION_COMPLETADA, misionCompletadaEvento.donante(), extraData);
  }
}

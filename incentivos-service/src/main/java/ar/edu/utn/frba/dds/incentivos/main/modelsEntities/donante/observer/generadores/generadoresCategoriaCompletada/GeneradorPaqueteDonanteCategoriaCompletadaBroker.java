package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.generadoresCategoriaCompletada;

import ar.edu.utn.frba.dds.common.messaging.EventsTypeEnum;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.CategoriaCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteCategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteBroker.PaqueteNotificacionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GeneradorPaqueteDonanteCategoriaCompletadaBroker implements IGeneradorPaqueteDonanteCategoriaCompletada {
  private final PaqueteNotificacionFactory paqueteNotificacionFactory;

  public GeneradorPaqueteDonanteCategoriaCompletadaBroker(PaqueteNotificacionFactory paqueteNotificacionFactory) {
    this.paqueteNotificacionFactory = paqueteNotificacionFactory;
  }

  @Override
  public Optional<Paquete> generarPaqueteSiCorresponde(CategoriaCompletadaEvento categoriaCompletadaEvento) {
    Map<String, String> extraData = new HashMap<>();
    if (categoriaCompletadaEvento.categoriaAnterior() != null && categoriaCompletadaEvento.categoriaAnterior().getCategoria() != null) {
      extraData.put("categoria", categoriaCompletadaEvento.categoriaAnterior().getCategoria().getNombre());
    }
    return paqueteNotificacionFactory.crear(EventsTypeEnum.CATEGORIA_COMPLETADA, categoriaCompletadaEvento.donante(), extraData);
  }
}

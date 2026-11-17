package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteBroker;

import ar.edu.utn.frba.dds.common.messaging.MessagePublisher;
import ar.edu.utn.frba.dds.common.messaging.NotificationDto;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.IPaqueteAdapter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PaqueteBrokerAdapter implements IPaqueteAdapter<PaqueteBroker> {
  private final MessagePublisher messagePublisher;
  private final JsonMapper jsonMapper;

  public PaqueteBrokerAdapter(MessagePublisher messagePublisher, JsonMapper jsonMapper) {
    this.messagePublisher = messagePublisher;
    this.jsonMapper = jsonMapper;
  }

  @Override
  public void enviar(PaqueteBroker paquete) {
    NotificationDto notificacion = jsonMapper.readValue(paquete.getPayload(), NotificationDto.class);
    String routingKey = paquete.getRoutingKey();
    // "notificacion.general" -> "GENERAL"
    String severidad = routingKey.substring(routingKey.lastIndexOf('.') + 1).toUpperCase();
    messagePublisher.notifyUser(severidad, notificacion);
  }

  @Override
  public Class<PaqueteBroker> tipoDePaqueteSoportado() {
    return PaqueteBroker.class;
  }

}

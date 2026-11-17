package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteBroker;

import ar.edu.utn.frba.dds.common.messaging.ContactMethodDto;
import ar.edu.utn.frba.dds.common.messaging.EventsTypeEnum;
import ar.edu.utn.frba.dds.common.messaging.NotificationDto;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.MedioDeContacto;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PaqueteNotificacionFactory {
  public static final String ROUTING_KEY = "notificacion.general";
  private static final String ORIGEN = "incentivos-service";

  private final JsonMapper jsonMapper;

  public PaqueteNotificacionFactory(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public Optional<Paquete> crear(EventsTypeEnum tipo, Donante donante, Map<String, String> extraData) {
    List<ContactMethodDto> contactos = donante.getMedioContacto() == null ? List.of()
        : donante.getMedioContacto().stream().map(this::mapContacto).toList();
    if (contactos.isEmpty()) {
      return Optional.empty();
    }

    UUID eventId = UUID.randomUUID();
    NotificationDto dto = new NotificationDto(eventId, tipo, ORIGEN, LocalDateTime.now(),
        donante.getNombreUsuario(), contactos, null, null, null, extraData);

    PropertiesBroker propiedades = new PropertiesBroker("application/json", eventId.toString(), 1, null);
    Map<String, String> headers = Map.of("sistema-origen", ORIGEN, "version-evento", "1.0");
    return Optional.of(new PaqueteBroker(ROUTING_KEY, jsonMapper.writeValueAsBytes(dto), propiedades, headers));
  }

  private ContactMethodDto mapContacto(MedioDeContacto contacto) {
    return new ContactMethodDto(contacto.getValor(), contacto.getTipo() != null ? contacto.getTipo().name() : null);
  }
}

package ar.edu.utn.frba.dds.donaciones.main.infrastructure.impl;

import ar.edu.utn.frba.dds.common.messaging.ContactMethodDto;
import ar.edu.utn.frba.dds.common.messaging.EventsTypeEnum;
import ar.edu.utn.frba.dds.common.messaging.MessagePublisher;
import ar.edu.utn.frba.dds.common.messaging.NotificationDto;
import ar.edu.utn.frba.dds.donaciones.main.dtos.notificacion.MedioDeContactoDto;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.NotificacionClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class NotificacionRabbitAdapter implements NotificacionClient {

    private static final String ORIGEN = "donaciones-service";
    private static final String SEVERIDAD_GENERAL = "GENERAL";
    private static final String SEVERIDAD_BULK = "BULK";

    private final MessagePublisher messagePublisher;

    public NotificacionRabbitAdapter(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void notificarInactividad(String nombre, List<MedioDeContactoDto> contactos) {
        messagePublisher.notifyUser(SEVERIDAD_BULK,
                armar(EventsTypeEnum.INACTIVIDAD, nombre, contactos, null, null, null));
    }

    @Override
    public void notificarAsignacionEntidad(String nombre, List<MedioDeContactoDto> contactos, String detalleDonacion) {
        messagePublisher.notifyUser(SEVERIDAD_GENERAL,
                armar(EventsTypeEnum.ASIGNACION_ENTIDAD, nombre, contactos, null, detalleDonacion, null));
    }

    @Override
    public void notificarAsignacionDonante(String nombre, List<MedioDeContactoDto> contactos, String detalleDonacion, String nombreEntidad) {
        messagePublisher.notifyUser(SEVERIDAD_GENERAL,
                armar(EventsTypeEnum.ASIGNACION_DONANTE, nombre, contactos, null, detalleDonacion, nombreEntidad));
    }

    @Override
    public void enviarNotificacionGeneral(String nombre, List<MedioDeContactoDto> contactos, String mensaje) {
        messagePublisher.notifyUser(SEVERIDAD_GENERAL,
                armar(EventsTypeEnum.GENERAL, nombre, contactos, mensaje, null, null));
    }

    private NotificationDto armar(EventsTypeEnum tipo, String nombre, List<MedioDeContactoDto> contactos,
                                  String mensaje, String detalleDonacion, String nombreEntidad) {
        List<ContactMethodDto> contactosEvento = contactos == null ? List.of()
                : contactos.stream().map(c -> new ContactMethodDto(c.getValor(), c.getTipo())).toList();
        return new NotificationDto(UUID.randomUUID(), tipo, ORIGEN, LocalDateTime.now(), nombre,
                contactosEvento, mensaje, detalleDonacion, nombreEntidad, null);
    }
}

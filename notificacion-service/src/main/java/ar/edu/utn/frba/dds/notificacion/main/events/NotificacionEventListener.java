package ar.edu.utn.frba.dds.notificacion.main.events;

import ar.edu.utn.frba.dds.common.messaging.ContactMethodDto;
import ar.edu.utn.frba.dds.common.messaging.EventsTypeEnum;
import ar.edu.utn.frba.dds.common.messaging.NotificationDto;
import ar.edu.utn.frba.dds.notificacion.main.services.NotificacionService;
import ar.edu.utn.frba.dds.notificacion.main.services.dtos.MedioDeContactoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificacionEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionEventListener.class);
    private final NotificacionService notificacionService;

    public NotificacionEventListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @RabbitListener(queues = {"${donatrack.events.queues.notificacion.critica}", "${donatrack.events.queues.notificacion.general}", "${donatrack.events.queues.notificacion.bulk}"})
    @PreAuthorize("hasAnyRole('DONACIONES_SERVICE', 'LOGISTICA_SERVICE', 'INCENTIVOS_SERVICE')")
    public void handleNotificacionEvent(NotificationDto notificationDto, @Header(AmqpHeaders.CONSUMER_QUEUE) String severity) {
        log.info("Enviando la notificación de la queue {}", severity);
        if (notificationDto.eventsTypeEnum() == null) {
            log.error("Evento sin tipo, id: {} en la cola {}", notificationDto.eventId(), severity);
            throw new AmqpRejectAndDontRequeueException("Se recibió un evento sin tipo");
        }
        try {
            dispatchNotificacion(notificationDto);
        } catch (Exception e) {
            // se puede mover el transactional outbox aquí
            log.error("Error enviando la notificación con id: {} de tipo {} en la cola {}", notificationDto.eventId(), notificationDto.eventsTypeEnum(), severity, e);
            // sin DLQ configurada, reencolar generaría un loop de reentregas
            throw new AmqpRejectAndDontRequeueException("Falló el envío de la notificación " + notificationDto.eventId(), e);
        }
    }

    public void dispatchNotificacion(NotificationDto request) {
        switch (request.eventsTypeEnum()) {
            case EventsTypeEnum.ENTREGA_EXITO:
            case EventsTypeEnum.ENTREGA_FALLIDA:
            case EventsTypeEnum.INICIO_RUTA:
                notificacionService.notifyTruckRoute(request.name(), mapContactsMethod(request.contacts()), request.eventsTypeEnum());
                break;
            case EventsTypeEnum.CATEGORIA_COMPLETADA:
            case EventsTypeEnum.MISION_COMPLETADA:
                String message = (request.eventsTypeEnum() == EventsTypeEnum.MISION_COMPLETADA) ? "misión" : "categoría";
                notificacionService.enviarNotificacionGeneral(request.name(), mapContactsMethod(request.contacts()), "Felicitaciones, " + request.name() + ", por haber completado la " + message);
                break;
            case EventsTypeEnum.INACTIVIDAD:
                notificacionService.notificarInactividad(request.name(), mapContactsMethod(request.contacts()));
                break;
            case EventsTypeEnum.ASIGNACION_ENTIDAD:
                notificacionService.notificarAsignacionEntidad(
                    request.name(), mapContactsMethod(request.contacts()), request.donationDetail());
                break;
            case EventsTypeEnum.ASIGNACION_DONANTE:
                notificacionService.notificarAsignacionDonante(
                    request.name(), mapContactsMethod(request.contacts()), request.donationDetail(), request.entityName());
                break;
            case EventsTypeEnum.GENERAL:
                notificacionService.enviarNotificacionGeneral(
                    request.name(), mapContactsMethod(request.contacts()), request.message());
                break;
            default:
                log.warn("Tipo de evento desconocido: {}", request.eventsTypeEnum());
        }
    }

    public List<MedioDeContactoDto> mapContactsMethod(List<ContactMethodDto> contacts) {
        if (contacts != null) return contacts.stream().map(z -> new MedioDeContactoDto(z.value(), z.type())).toList();
        return List.of();
    }
}

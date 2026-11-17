package ar.edu.utn.frba.dds.notificacion.main.events;

import ar.edu.utn.frba.dds.notificacion.main.services.NotificacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacionesEventListener {
    private static final Logger log = LoggerFactory.getLogger(NotificacionesEventListener.class);
    private final NotificacionService notificacionService;

    public NotificacionesEventListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

//    @RabbitListener(queues = "${donatrack.events.queues.notificaciones}")
//    public void onNotificarUsuario(NotificacionDonanteEntidadDto notificacionDto) {
//        log.info("Se ejecuta el listener para la cola de notificaciones");
//        List<MedioDeContactoDto> medioDeContactoDto = List.of(new MedioDeContactoDto(notificacionDto.medioDeContacto().getValor(), notificacionDto.medioDeContacto().getTipo()));
//        this.notificacionService.enviarNotificacionGeneral("", medioDeContactoDto, notificacionDto.mensaje());
//    }
}

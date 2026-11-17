package ar.edu.utn.frba.dds.notificacion.main.controllers;

import ar.edu.utn.frba.dds.notificacion.main.controllers.dtos.NotificacionRequestDto;
import ar.edu.utn.frba.dds.notificacion.main.services.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @PostMapping("/enviar")
    public String enviarNotificacion(@RequestBody NotificacionRequestDto request) {
        notificacionService.enviarNotificacionGeneral(
                request.getNombre(),
                request.getContactos(),
                request.getMensaje()
        );
        return "Notificación procesada correctamente para " + request.getNombre();
    }

    @PostMapping("/inactividad")
    public String notificarInactividad(@RequestBody NotificacionRequestDto request) {
        notificacionService.notificarInactividad(request.getNombre(), request.getContactos());
        return "Notificación de inactividad enviada a " + request.getNombre();
    }

    @PostMapping("/asignacion-entidad")
    public String notificarAsignacionEntidad(@RequestBody NotificacionRequestDto request) {
        notificacionService.notificarAsignacionEntidad(
                request.getNombre(),
                request.getContactos(),
                request.getDetalleDonacion()
        );
        return "Notificación de asignación enviada a la entidad " + request.getNombre();
    }

    @PostMapping("/asignacion-donante")
    public String notificarAsignacionDonante(@RequestBody NotificacionRequestDto request) {
        notificacionService.notificarAsignacionDonante(
                request.getNombre(),
                request.getContactos(),
                request.getDetalleDonacion(),
                request.getNombreEntidad()
        );
        return "Notificación de asignación enviada al donante " + request.getNombre();
    }
}


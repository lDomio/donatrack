package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.common.messaging.EventsTypeEnum;
import ar.edu.utn.frba.dds.notificacion.main.services.dtos.MedioDeContactoDto;

import java.util.List;

public interface NotificacionService {
    void notificarInactividad(String nombreDestinatario, List<MedioDeContactoDto> contactos);

    void notificarAsignacionEntidad(String nombreEntidad, List<MedioDeContactoDto> contactos, String detalleDonacion);

    void notificarAsignacionDonante(String nombreDonante, List<MedioDeContactoDto> contactosDonante, String detalleDonacion, String nombreEntidad);

    void enviarNotificacionGeneral(String nombreDestinatario, List<MedioDeContactoDto> contactos, String mensaje);

    void notifyTruckRoute(String name, List<MedioDeContactoDto> contacts, EventsTypeEnum routeStatus);
}


package ar.edu.utn.frba.dds.donaciones.main.infrastructure;

import ar.edu.utn.frba.dds.donaciones.main.dtos.notificacion.MedioDeContactoDto;
import java.util.List;

public interface NotificacionClient {
    void notificarInactividad(String nombre, List<MedioDeContactoDto> contactos);
    void notificarAsignacionEntidad(String nombre, List<MedioDeContactoDto> contactos, String detalleDonacion);
    void notificarAsignacionDonante(String nombre, List<MedioDeContactoDto> contactos, String detalleDonacion, String nombreEntidad);
    void enviarNotificacionGeneral(String nombre, List<MedioDeContactoDto> contactos, String mensaje);
}

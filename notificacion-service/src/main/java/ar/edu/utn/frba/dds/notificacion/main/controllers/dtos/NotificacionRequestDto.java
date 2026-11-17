package ar.edu.utn.frba.dds.notificacion.main.controllers.dtos;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.TipoDestinatario;
import ar.edu.utn.frba.dds.notificacion.main.services.dtos.MedioDeContactoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionRequestDto {
    private Long idDestinatarioOrigen;
    private TipoDestinatario tipoDestinatario;
    private String nombre;
    private String mensaje;
    private List<MedioDeContactoDto> contactos;

    // Campos opcionales para notificaciones de dominio (Asignaciones)
    private String detalleDonacion;
    private String nombreEntidad;

    // Tipo de evento para consumo via RabbitMQ (INACTIVIDAD, ASIGNACION_ENTIDAD, ASIGNACION_DONANTE, GENERAL)
    private String tipoEvento;
}

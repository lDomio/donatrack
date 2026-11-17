package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.common.messaging.EventsTypeEnum;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.Notificador;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.Destinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.gestorNotificacion.GestorNotificaciones;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.MedioDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.PlantillaCanal;
import ar.edu.utn.frba.dds.notificacion.main.repositories.NotificacionRepository;
import ar.edu.utn.frba.dds.notificacion.main.services.dtos.MedioDeContactoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private List<Notificador> notificadores;

    @Autowired
    private NotificacionRepository repository;

    @Autowired
    private Environment env;

    @Override
    public void notificarInactividad(String nombreDestinatario, List<MedioDeContactoDto> contactos) {
        try {
            Notificacion notificacion = crearNotificacionBase(nombreDestinatario, contactos);
            notificacion.setMensaje("Hola " + nombreDestinatario + ", hace tiempo que no sabemos de vos. ¡Incentivate a realizar una nueva donación!");
            enviar(notificacion);
        } catch (Exception e) {
            System.err.println("Error en notificarInactividad: " + e.getMessage());
        }
    }

    @Override
    public void notificarAsignacionEntidad(String nombreEntidad, List<MedioDeContactoDto> contactos, String detalleDonacion) {
        try {
            Notificacion notificacion = crearNotificacionBase(nombreEntidad, contactos);

            PlantillaCanal plantilla = notificacion.getPlantillaCanal();
            Map<String, String> variables = plantilla.getVariables();
            variables.put("1", nombreEntidad);
            variables.put("2", detalleDonacion);
            plantilla.setNombrePlantilla("DONACION_ASIGNADA_ENTIDAD");

            enviar(notificacion);
        } catch (Exception e) {
            System.err.println("Error en notificarAsignacionEntidad: " + e.getMessage());
        }
    }

    @Override
    public void notificarAsignacionDonante(String nombreDonante, List<MedioDeContactoDto> contactosDonante, String detalleDonacion, String nombreEntidad) {
        try {
            Notificacion notificacion = crearNotificacionBase(nombreDonante, contactosDonante);

            PlantillaCanal plantilla = notificacion.getPlantillaCanal();
            Map<String, String> variables = plantilla.getVariables();
            variables.put("1", nombreDonante);
            variables.put("2", detalleDonacion);
            variables.put("3", nombreEntidad);
            plantilla.setNombrePlantilla("DONACION_ASIGNADA_DONANTE");
            enviar(notificacion);
        } catch (Exception e) {
            System.err.println("Error en notificarAsignacionDonante: " + e.getMessage());
        }
    }

    @Override
    public void enviarNotificacionGeneral(String nombreDestinatario, List<MedioDeContactoDto> contactos, String mensaje) {
        try {
            Notificacion notificacion = crearNotificacionBase(nombreDestinatario, contactos);
            notificacion.setMensaje(mensaje);
            enviar(notificacion);
        } catch (Exception e) {
            System.err.println("Error en enviarNotificacionGeneral: " + e.getMessage());
        }
    }

    @Override
    public void notifyTruckRoute(String name, List<MedioDeContactoDto> contacts, EventsTypeEnum routeStatus) {
        try {
            Notificacion notification = crearNotificacionBase(name, contacts);
            String message = switch (routeStatus) {
                case EventsTypeEnum.ENTREGA_EXITO ->
                        "Hola, " + name + ". Queríamos avisarte que el tu donación fue entregada con éxito!";
                case EventsTypeEnum.ENTREGA_FALLIDA ->
                        "Hola, " + name + ". Tuvimos un inconveniente para entregar tu pedido! Podes ver la razón vía web";
                case EventsTypeEnum.INICIO_RUTA ->
                        "Hola, " + name + ". Queríamos avisarte que tu donación salió en camino!";
                default -> throw new IllegalStateException("Unexpected value: " + routeStatus);
            };
            notification.setMensaje(message);
            enviar(notification);
        } catch (Exception e) {
            System.err.println("Error en enviarNotificacionGeneral: " + e.getMessage());
        }
    }

    private Notificacion crearNotificacionBase(String nombre, List<MedioDeContactoDto> contactosDto) {
        List<MedioDeContacto> contactosMapeados = new ArrayList<>();
        if (contactosDto != null) {
            contactosMapeados = contactosDto.stream()
                .map(this::mapearMedioDeContacto)
                .collect(Collectors.toList());
        }

        Destinatario destinatario = new Destinatario(nombre, contactosMapeados);
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setEstadoNotificaciones(new ArrayList<>());

        PlantillaCanal plantilla = new PlantillaCanal();
        plantilla.setAsunto("Notificación de DonaTrack");
        plantilla.setRemitente("no-reply@donatrack.org");
        notificacion.setPlantillaCanal(plantilla);

        return notificacion;
    }

    private void enviar(Notificacion notificacion) {
        if (notificadores == null || notificadores.isEmpty()) {
            System.err.println("No hay notificadores configurados");
            return;
        }

        // 1. Guardamos la notificación inicialmente (Estado Pendiente/Creada)
        repository.save(notificacion);

        // 2. El Gestor se encarga de enviar y actualizar el objeto Notificacion con nuevos estados
        GestorNotificaciones gestorNotificaciones = new GestorNotificaciones(notificadores);
        gestorNotificaciones.despachadorDeNotificaciones(notificacion, notificacion.getMensaje());

        // 3. Volvemos a guardar para persistir los cambios de estado (ENVIADA o ERROR)
        repository.save(notificacion);
    }

    private MedioDeContacto mapearMedioDeContacto(MedioDeContactoDto dto) {
        TipoDeContacto tipoMapeado;
        try {
            tipoMapeado = TipoDeContacto.valueOf(dto.getTipo().toUpperCase());
        } catch (Exception e) {
            tipoMapeado = TipoDeContacto.SMS;
        }
        return new MedioDeContacto(dto.getValor(), tipoMapeado, true);
    }
}


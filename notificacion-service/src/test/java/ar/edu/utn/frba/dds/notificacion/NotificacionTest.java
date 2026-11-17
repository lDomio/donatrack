package ar.edu.utn.frba.dds.notificacion;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales.EmailCanal;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales.MensajeCanal;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.Notificador;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales.WhatsappCanal;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.Destinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.estadosNotificacion.TipoDeEstado;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.gestorNotificacion.GestorNotificaciones;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.MedioDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.PlantillaCanal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificacionTest {
/*
  private GestorNotificaciones gestor;
  private Destinatario destinatario;

  @BeforeEach
  public void setUp() {
    WhatsappCanal whatsappMock = mock(WhatsappCanal.class);
    when(whatsappMock.soporta(TipoDeContacto.WHATSAPP)).thenReturn(true);
    when(whatsappMock.enviar(any(), anyString())).thenReturn(true);

    List<Notificador> notificadores = Arrays.asList(
        new EmailCanal(),
        whatsappMock,
        new MensajeCanal()
    );
    gestor = new GestorNotificaciones(notificadores);

    List<MedioDeContacto> medios = new ArrayList<>();
    medios.add(new MedioDeContacto("juan@gmail.com", TipoDeContacto.EMAIL, true));
    medios.add(new MedioDeContacto("+541122334455", TipoDeContacto.WHATSAPP, true));
    medios.add(new MedioDeContacto("+541122334455", TipoDeContacto.SMS, false));

    destinatario = new Destinatario("Juan Perez", medios);
  }

  @Test
  public void testDespacharNotificacionAContactosPreferidos() {
    PlantillaCanal plantilla = new PlantillaCanal(
        "Aviso de Donación",
        "Hola Juan, gracias por tu donación.",
        "DonaTrack",
        null, null, null, null,
        LocalDateTime.now(),
        TipoDeContacto.EMAIL
    );

    Notificacion notificacion = new Notificacion(
        destinatario,
        "Hola Juan, gracias por tu donación.",
        plantilla,
        new ArrayList<>()
    );

    gestor.despachadorDeNotificaciones(notificacion, notificacion.getMensaje());

    // Se debería haber enviado por EMAIL y WHATSAPP (preferidos)
    Assertions.assertEquals(2, notificacion.getEstadoNotificaciones().size());
    Assertions.assertEquals(TipoDeEstado.ENVIADA, notificacion.obtenerUltimoEstado().getTipo());
  }
  */
}

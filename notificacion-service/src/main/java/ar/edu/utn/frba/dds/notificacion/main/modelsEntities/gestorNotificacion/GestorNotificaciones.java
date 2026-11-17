package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.gestorNotificacion;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.Notificador;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.Destinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.estadosNotificacion.TipoDeEstado;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.MedioDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;

import java.util.List;

public class GestorNotificaciones {

  private List<Notificador> notificadores;

  public GestorNotificaciones(List<Notificador> notificadores) {
    this.notificadores = notificadores;
  }

  public void despachadorDeNotificaciones(Notificacion notificacion, String mensaje) {
    Destinatario destinatario = notificacion.getDestinatario();
    List<MedioDeContacto> contactosPreferidos = destinatario.obtenerContactosPreferidos();

    for (MedioDeContacto medio : contactosPreferidos) {
      notificadores.stream()
          .filter(notificador -> notificador.soporta(medio.getTipoDeContacto()))
          .findFirst()
          .ifPresent(notificador -> {
            boolean exito = notificador.enviar(notificacion, medio.getValor());
            if (exito) {
              notificacion.registrarEvento(TipoDeEstado.ENVIADA);
            } else {
              notificacion.registrarEvento(TipoDeEstado.ERROR);
            }
          });
    }
  }
}

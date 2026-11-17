package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;

public interface AdapterEnviarMail {
  boolean enviar(Notificacion notificacion, String destinatario);
}

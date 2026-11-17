package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;

public interface EmailService {
  void enviarEmail(Notificacion notificacion, String contacto);
}

package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;

public interface SmsService {
  void enviarSms(Notificacion notificacion, String contacto);
}

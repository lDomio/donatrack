package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;

public interface WhatsappService {
  void enviarWhatsapp(Notificacion notificacion, String contacto);
}

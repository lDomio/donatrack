package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales.WhatsappCanal;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WhatsappServiceImpl implements WhatsappService {

  @Autowired
  private WhatsappCanal whatsappCanal;

  @Override
  public void enviarWhatsapp(Notificacion notificacion, String contacto) {
    whatsappCanal.enviar(notificacion, contacto);
  }
}

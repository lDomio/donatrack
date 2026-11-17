package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales.MensajeCanal;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

  @Autowired
  private MensajeCanal mensajeCanal;

  @Override
  public void enviarSms(Notificacion notificacion, String contacto) {
    mensajeCanal.enviar(notificacion, contacto);
  }
}

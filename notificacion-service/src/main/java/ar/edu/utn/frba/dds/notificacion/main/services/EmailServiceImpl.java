package ar.edu.utn.frba.dds.notificacion.main.services;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

  public EmailServiceImpl() {
  }

  @Override
  public void enviarEmail(Notificacion notificacion, String contacto) {
    System.out.println("[MOCK] Simulando envío de EMAIL a: " + contacto);
    System.out.println("[MOCK] Asunto: " + notificacion.getPlantillaCanal().getAsunto());
    System.out.println("[MOCK] Cuerpo: " + notificacion.getMensaje());
  }
}

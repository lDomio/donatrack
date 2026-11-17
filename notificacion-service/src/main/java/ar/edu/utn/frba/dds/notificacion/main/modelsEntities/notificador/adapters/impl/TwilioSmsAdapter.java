package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.impl;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.AdapterEnviarSms;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TwilioSmsAdapter implements AdapterEnviarSms {
  @Autowired
  private Environment env;

  @Override
  public boolean enviar(Notificacion notificacion, String destinatario) {
    String accountSid = env.getProperty("TWILIO_ACCOUNT_SID");
    String authToken = env.getProperty("TWILIO_AUTH_TOKEN");
    String fromNumber = env.getProperty("TWILIO_SMS_FROM");

    System.out.println("[DEBUG] Intentando enviar SMS con Twilio...");
    System.out.println("[DEBUG] Account SID: " + (accountSid != null ? accountSid.substring(0, 5) + "..." : "MISSING"));
    System.out.println("[DEBUG] From Number: " + (fromNumber != null ? fromNumber : "MISSING"));

    try {
      if (accountSid == null || authToken == null || fromNumber == null) {
        System.out.println("[MOCK] SMS simulado (faltan credenciales o remitente): " + destinatario);
        return true;
      }
      Twilio.init(accountSid, authToken);

      String cleanDest = destinatario.trim().replaceAll("\\s+", "").replaceAll("-", "");

      if (!cleanDest.startsWith("+")) {
        cleanDest = "+" + cleanDest;
      }

      // Si es +549XXXXXXXXXX (13 dígitos), le sacamos el 9 para que quede +54XXXXXXXXXX (12 dígitos)
      if (cleanDest.startsWith("+549") && cleanDest.length() == 14) {
        cleanDest = "+54" + cleanDest.substring(4);
        System.out.println("[DEBUG] Aplicado fix Argentina: removido el '9' del destinatario -> " + cleanDest);
      }

      System.out.println("[DEBUG] Enviando SMS a: " + cleanDest + " desde: " + fromNumber);

      MessageCreator creator;

      // Verificamos si la notificación tiene una plantilla con SID de Twilio para usar la Content API
      if (notificacion.getPlantillaCanal() != null && notificacion.getPlantillaCanal().getNombrePlantilla() != null) {
        String contentSid = null;
        String nombreSid = notificacion.getPlantillaCanal().getNombrePlantilla();
        String variablesJson = notificacion.getPlantillaCanal().getVariablesAsJson();

        if("DONACION_ASIGNADA_ENTIDAD".equals(nombreSid)){
          contentSid = env.getProperty("TWILIO_DONACION_ASIGNADA_ENTIDAD");
        }else if("DONACION_ASIGNADA_DONANTE".equals(nombreSid)){
          contentSid = env.getProperty("TWILIO_DONACION_ASIGNADA_DONANTE");
        }

        if (contentSid != null) {
          System.out.println("[DEBUG] Usando Content API para SMS (Template): " + contentSid);
          creator = Message.creator(new PhoneNumber(cleanDest), new PhoneNumber(fromNumber), "")
              .setContentSid(contentSid)
              .setContentVariables(variablesJson);
        } else {
          creator = Message.creator(new PhoneNumber(cleanDest), new PhoneNumber(fromNumber), notificacion.getMensaje());
        }
      } else {
          // Si no hay SID, enviamos un mensaje de texto tradicional
          System.out.println("[DEBUG] Usando SMS Tradicional (Texto Plano)");
          creator = Message.creator(
              new PhoneNumber(cleanDest),
              new PhoneNumber(fromNumber),
              notificacion.getMensaje()
          );
      }

      Message message = creator.create();

      System.out.println("SMS enviado con éxito. SID: " + message.getSid());
      return true;
    } catch (Exception e) {
      System.err.println("Error enviando SMS a " + destinatario + ": " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}

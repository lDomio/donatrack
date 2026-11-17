package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.impl;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.AdapterEnviarWhatsapp;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TwilioWhatsappAdapter implements AdapterEnviarWhatsapp {

  @Autowired
  private Environment env;

  @Override
  public boolean enviar(Notificacion notificacion, String destinatario) {
    String accountSid = env.getProperty("TWILIO_ACCOUNT_SID");
    String authToken = env.getProperty("TWILIO_AUTH_TOKEN");
    String fromNumber = env.getProperty("TWILIO_WHATSAPP_FROM");

    System.out.println("[DEBUG] Intentando enviar WhatsApp con Twilio...");
    System.out.println("[DEBUG] Account SID: " + (accountSid != null ? accountSid.substring(0, 5) + "..." : "MISSING"));
    System.out.println("[DEBUG] From Number: " + (fromNumber != null ? fromNumber : "MISSING"));

    try {
      if (accountSid == null || authToken == null || fromNumber == null) {
        System.out.println("[MOCK] WhatsApp simulado (faltan credenciales o remitente): " + destinatario);
        return true;
      }

      Twilio.init(accountSid, authToken);

      // Limpiamos y formateamos el destinatario
      String cleanDest = destinatario.trim().replaceAll("\\s+", "").replaceAll("-", "");
      if (!cleanDest.startsWith("whatsapp:")) {
        cleanDest = "whatsapp:" + (cleanDest.startsWith("+") ? "" : "+") + cleanDest;
      }

      // Limpiamos y formateamos el remitente
      String cleanFrom = fromNumber.trim().replaceAll("\\s+", "").replaceAll("-", "");
      if (!cleanFrom.startsWith("whatsapp:")) {
        cleanFrom = "whatsapp:" + (cleanFrom.startsWith("+") ? "" : "+") + cleanFrom;
      }

      System.out.println("[DEBUG] Enviando WhatsApp a: " + cleanDest + " desde: " + cleanFrom);

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
          System.out.println("[DEBUG] Usando Content API para WhatsApp (Template): " + contentSid);
          creator = Message.creator(
                  new PhoneNumber(cleanDest),
                  new PhoneNumber(cleanFrom),
                  ""
              )
              .setContentSid(contentSid)
              .setContentVariables(variablesJson);
        } else {
          System.out.println("[WARN] No se encontró SID para " + nombreSid + ". Usando texto plano.");
          creator = Message.creator(new PhoneNumber(cleanDest), new PhoneNumber(cleanFrom), notificacion.getMensaje());
        }
      } else {
        // Si no hay SID, enviamos un mensaje de texto tradicional
        System.out.println("[DEBUG] Usando WhatsApp Tradicional (Texto Plano)");
        creator = Message.creator(
            new PhoneNumber(cleanDest),
            new PhoneNumber(cleanFrom),
            notificacion.getMensaje()
        );
      }

      Message message = creator.create();
      System.out.println("[DEBUG] WhatsApp enviado exitosamente. SID: " + message.getSid());
      return true;

    } catch (Exception e) {
      System.err.println("[ERROR] Error al enviar WhatsApp con Twilio: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}
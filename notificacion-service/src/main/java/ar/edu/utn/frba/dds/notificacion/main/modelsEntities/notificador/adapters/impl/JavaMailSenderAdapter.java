package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.impl;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.AdapterEnviarMail;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.ArchivoAdjunto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class JavaMailSenderAdapter implements AdapterEnviarMail {

  @Autowired(required = false)
  private JavaMailSender mailSender;

  public JavaMailSenderAdapter() {
  }

  public JavaMailSenderAdapter(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public boolean enviar(Notificacion notificacion, String destinatario) {
    if (mailSender == null) {
      System.out.println("[MOCK] JavaMailSender no configurado. Email simulado a: " + destinatario);
      return true;
    }

    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      // true indica que es multipart (para adjuntos)
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom(notificacion.getPlantillaCanal().getRemitente());
      helper.setTo(destinatario);
      helper.setSubject(notificacion.getPlantillaCanal().getAsunto());
      // true indica que el contenido es HTML
      helper.setText(notificacion.getMensaje(), true);

      // Agregamos adjuntos si existen
      if (notificacion.getPlantillaCanal().getArchivosAdjuntos() != null) {
        for (ArchivoAdjunto adjunto : notificacion.getPlantillaCanal().getArchivosAdjuntos()) {
          // Asumimos que el nombreArchivo es el path local por ahora
          File file = new File(adjunto.getNombreArchivo());
          if (file.exists()) {
            FileSystemResource res = new FileSystemResource(file);
            helper.addAttachment(file.getName(), res);
          }
        }
      }

      mailSender.send(mimeMessage);
      System.out.println("EMAIL (Real) enviado con éxito a " + destinatario);
      return true;
    } catch (Exception e) {
      System.err.println("Error enviando email real en JavaMailSenderAdapter: " + e.getMessage());
      return false;
    }
  }
}

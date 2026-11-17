package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteN8N;


import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.IPaqueteAdapter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaqueteN8NAdapter implements IPaqueteAdapter<PaqueteN8N> {
  @Autowired
  private RestTemplate restTemplate;

  @Value("${n8n.webhook.url}")
  private String urlN8n;

  @Override
  public void enviar(PaqueteN8N paquete) {
    HttpHeaders headers = new HttpHeaders();

    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, String>> request = new HttpEntity<>(paquete.getMensajeJson(), headers);
    try{
      System.out.println("Enviando insignia: " + paquete.getMensajeJson().get("nombreInsignia") + " a n8n.");
      restTemplate.postForObject(urlN8n, request, String.class);
      System.out.println("Mensaje enviado exitosamente a n8n.");
    }
    catch (Exception e){
      paquete.avisarEnvioFallido();
      System.out.println("Error al enviar el mensaje. Intento número: " + paquete.getCantidadDeIntentos());
      System.out.println("Motivo del error: " + e.getMessage());
      throw new RuntimeException("No se pudo enviar el paquete a n8n", e);
    }
  }

  @Override
  public Class<PaqueteN8N> tipoDePaqueteSoportado() {
    return PaqueteN8N.class;
  }
}

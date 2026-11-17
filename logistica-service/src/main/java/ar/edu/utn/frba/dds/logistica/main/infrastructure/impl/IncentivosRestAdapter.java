package ar.edu.utn.frba.dds.logistica.main.infrastructure.impl;

import ar.edu.utn.frba.dds.common.messaging.RestApiCallerService;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.IncentivosClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentivosRestAdapter implements IncentivosClient {
  private final RestApiCallerService restApiCallerService;
  private final String donacionesServiceUri;

  public IncentivosRestAdapter(RestApiCallerService restApiCallerService, @Value("${services.incentivos.url}") String donacionesServiceUri) {
    this.restApiCallerService = restApiCallerService;
    this.donacionesServiceUri = donacionesServiceUri;
  }

  @Override
  public void donacionRecibida(Long id, String nombreDeLaEntidad){
    String path = "/donacion/" + id + "?nombreDeLaEntidad=" + nombreDeLaEntidad;
    String uri = this.donacionesServiceUri + path;
    try {
      restApiCallerService.put(uri, null);
    } catch (Exception e) {
      System.err.println("Error al comunicar con Incentivos Service: " + e.getMessage());
    }
  }
}

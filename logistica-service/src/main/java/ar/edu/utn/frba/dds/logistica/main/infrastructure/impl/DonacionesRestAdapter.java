package ar.edu.utn.frba.dds.logistica.main.infrastructure.impl;

import ar.edu.utn.frba.dds.common.messaging.RestApiCallerService;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.DonacionesClient;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.dtos.CambiarEstadoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
public class DonacionesRestAdapter implements DonacionesClient {
    private final RestApiCallerService restApiCallerService;
    private final String donacionesServiceUri;

    public DonacionesRestAdapter(RestApiCallerService restApiCallerService, @Value("${services.donaciones.url}") String donacionesServiceUri) {
      this.restApiCallerService = restApiCallerService;
      this.donacionesServiceUri = donacionesServiceUri;
    }

    @Override
    public void actualizarEstado(Long donacionId, String nuevoEstado, String motivo, String autor) {
        String base = this.donacionesServiceUri.endsWith("/donaciones-service")
            ? this.donacionesServiceUri
            : this.donacionesServiceUri + "/donaciones-service";
        String uri = base + "/donaciones/" + donacionId + "/estado";

        CambiarEstadoRequest request = new CambiarEstadoRequest(
            motivo,
            LocalDate.now(),
            nuevoEstado,
            autor
        );
        try {
            restApiCallerService.patch(uri, request, Void.class);
        } catch (Exception e) {
            System.err.println("Error al comunicar con Donaciones Service: " + e.getMessage());
        }
    }
}

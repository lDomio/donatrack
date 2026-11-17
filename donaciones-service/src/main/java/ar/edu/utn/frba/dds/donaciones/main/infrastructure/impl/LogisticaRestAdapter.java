package ar.edu.utn.frba.dds.donaciones.main.infrastructure.impl;

import ar.edu.utn.frba.dds.common.messaging.RestApiCallerService;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.LogisticaClient;
import ar.edu.utn.frba.dds.donaciones.main.services.impl.dto.DonacionAsignadaRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Slf4j
@Component
public class LogisticaRestAdapter implements LogisticaClient {
  private final RestApiCallerService restApiCallerService;
  private final String logisticaServiceUri;
  private final int intentosMaximos = 3;

  public LogisticaRestAdapter(RestApiCallerService restApiCallerService, @Value("${services.logistica.url}") String logisticaServiceUri) {
    this.restApiCallerService = restApiCallerService;
    this.logisticaServiceUri = logisticaServiceUri;
  }

  @Override
  public void donacionAsignada(Long idDonacion, Long idDonante, String subcategoriaDonacion, Double cantidad, LocalDate fechaVencimiento, String razonSocialEntidad, Long idEntidad, String direccionEntidad, Double latitud, Double longitud) {
    String crearDonacionAsignada = "/logistica-service/recepcion/asignacion";
    String uri = this.logisticaServiceUri + crearDonacionAsignada;
    log.info("[LogisticaAdapter] Enviando donacion asignada a Logistica: idDonacion={}, idDonante={}, subcat={}, cantidad={}, entidad={}, uri={}",
            idDonacion, idDonante, subcategoriaDonacion, cantidad, razonSocialEntidad, uri);

    int intentosActuales = 0;
    long tiempoDeEspera = 1000;

    while (intentosActuales < intentosMaximos) {
      try {
        DonacionAsignadaRequest donacion = new DonacionAsignadaRequest(
                idDonacion,
                idDonante,
                subcategoriaDonacion,
                cantidad,
                fechaVencimiento,
                idEntidad,
                razonSocialEntidad,
                direccionEntidad,
                latitud,
                longitud
        );
        restApiCallerService.post(uri, donacion, Void.class);
        log.info("[LogisticaAdapter] Donacion asignada enviada con exito a Logistica: idDonacion={}", idDonacion);
        break;
      } catch (HttpServerErrorException e) {
        intentosActuales++;
        log.warn("[LogisticaAdapter] Error servidor al enviar asignacion (intento {}/{}): {}", intentosActuales, intentosMaximos, e.getMessage());
        if (intentosActuales == intentosMaximos) {
          throw e;
        }
        try {
          Thread.sleep(tiempoDeEspera);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException("Hilo interrumpido durante el reintento", ie);
        }
        tiempoDeEspera *= 2;
      } catch (HttpClientErrorException e) {
        log.error("[LogisticaAdapter] Error cliente 4xx al enviar asignacion a Logistica: {}", e.getMessage());
        throw e;
      } catch (RuntimeException e) {
        log.error("[LogisticaAdapter] Error de comunicacion con Logistica Service: {}", e.getMessage());
        break;
      }
    }
  }
}

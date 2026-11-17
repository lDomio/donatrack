package ar.edu.utn.frba.dds.logistica.main.infrastructure.impl;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.PollingRoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.RequestApiRutas;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.OptimizadorRutasAdapter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class OptimizadorRutasRestAdapter implements OptimizadorRutasAdapter {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    @Setter
    private int intentosMaximos = 3;

    public OptimizadorRutasRestAdapter(
            RestTemplate restTemplate,
            @Value("${rutasApi.url}") String apiUrl,
            @Value("${rutasApi.apiKey}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

  @Override
    public void solicitarPlanificacion(RequestApiRutas request) {
        String uri = this.apiUrl + "/v1/plan-route";
        comunicarseConApi(uri, request, Void.class, HttpMethod.POST);
    }

    @Override
    public PollingRoutingResult consultarEstado(String requestId) {
        String uri = this.apiUrl + "/v1/plan-route/" + requestId;
        ResponseEntity<PollingRoutingResult> response = comunicarseConApi(uri, null, PollingRoutingResult.class, HttpMethod.GET);
        return response != null ? response.getBody() : null;
    }

    private <T> ResponseEntity<T> comunicarseConApi(String uri, RequestApiRutas request, Class<T> responseType, HttpMethod metodo) {
        int intentosActuales = 0;
        long tiempoDeEspera = 1000;
        while (intentosActuales < intentosMaximos) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);
                System.out.println(uri);

                return restTemplate.exchange(
                        uri,
                        metodo,
                        new HttpEntity<>(request, headers),
                        responseType
                );
            } catch (HttpServerErrorException e) {
                intentosActuales++;
                if (intentosActuales == intentosMaximos) { throw e; }
                try {
                    Thread.sleep(tiempoDeEspera);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Hilo interrumpido durante el reintento", ie);
                }
                tiempoDeEspera *= 2;
            } catch (HttpClientErrorException e) {
                throw e;
            } catch (RuntimeException e) {
                System.err.println("Error de comunicación con Api de rutas: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.out.println("Error al generar la firma: " + e.getMessage());
                throw new RuntimeException("Error inesperado en postApi", e);
            }
        }
        return null;
    }
}
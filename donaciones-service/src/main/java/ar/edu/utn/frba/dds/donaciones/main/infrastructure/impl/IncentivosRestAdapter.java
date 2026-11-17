package ar.edu.utn.frba.dds.donaciones.main.infrastructure.impl;

import ar.edu.utn.frba.dds.common.messaging.RestApiCallerService;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.ActualizarDonacionIncentivos;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.incentivos.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.incentivos.CrearDonanteRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.incentivos.MedioDeContactoRequest;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.IncentivosClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class IncentivosRestAdapter implements IncentivosClient {

    private final RestApiCallerService restApiCallerService;
    private final String incentivosServiceUri;
    private final int intentosMaximos = 3;

    public IncentivosRestAdapter(RestApiCallerService restApiCallerService, @Value("${services.incentivos.url}") String incentivosServiceUri) {
      this.restApiCallerService = restApiCallerService;
        this.incentivosServiceUri = incentivosServiceUri;
    }

    @Override
    public void agregarDonacion(Long idDonacion, Long idDonante, Double cantidadBienes, String nombreSubcategoria, String nombreCategoria, LocalDate fecha, Long idEntidadBeneficiaria) {
        String crearDonacionPath = "/donacion";
        String uri = this.incentivosServiceUri + crearDonacionPath;
        log.info("[IncentivosAdapter] Enviando donacion creada a Incentivos: idDonacion={}, idDonante={}, cantidad={}, subcat={}, cat={}, uri={}",
                idDonacion, idDonante, cantidadBienes, nombreSubcategoria, nombreCategoria, uri);
        int intentosActuales = 0;
        long tiempoDeEspera = 1000;
        while (intentosActuales < intentosMaximos) {
            try {
                CrearDonacionRequest donacion = new CrearDonacionRequest(idDonacion, idDonante, fecha, cantidadBienes, nombreSubcategoria, nombreCategoria, idEntidadBeneficiaria);
                restApiCallerService.post(uri, donacion, Void.class);
                log.info("[IncentivosAdapter] Donacion enviada con exito a Incentivos: idDonacion={}", idDonacion);
                break;
            } catch (HttpServerErrorException e) {
                intentosActuales++;
                log.warn("[IncentivosAdapter] Error servidor al enviar donacion a Incentivos (intento {}/{}): {}", intentosActuales, intentosMaximos, e.getMessage());
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
                log.error("[IncentivosAdapter] Error 4xx al enviar donacion a Incentivos: {}", e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("[IncentivosAdapter] Error de comunicación con Incentivos Service: {}", e.getMessage());
                break;
            }
        }
    }

    @Override
    public void actualizarDonacion(Long idDonacion, String razonSocialEntidadBeneficiaria) {
        String path = "/donacion/" + idDonacion + "?nombreDeLaEntidad=" + razonSocialEntidadBeneficiaria;
        String uri = this.incentivosServiceUri + path;
        log.info("[IncentivosAdapter] Actualizando asignacion de donacion en Incentivos: idDonacion={}, entidad={}, uri={}",
                idDonacion, razonSocialEntidadBeneficiaria, uri);
        int intentosActuales = 0;
        long tiempoDeEspera = 1000;
        while (intentosActuales < intentosMaximos) {
            try {
                restApiCallerService.put(uri, null);
                log.info("[IncentivosAdapter] Asignacion actualizada con exito en Incentivos: idDonacion={}", idDonacion);
                break;
            } catch (HttpServerErrorException e) {
                intentosActuales++;
                log.warn("[IncentivosAdapter] Error servidor al actualizar donacion en intento {}/{}: {}", intentosActuales, intentosMaximos, e.getMessage());
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
                log.error("[IncentivosAdapter] Error cliente 4xx al actualizar donacion en Incentivos: {}", e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("[IncentivosAdapter] Error de comunicación con Incentivos Service: {}", e.getMessage());
                break;
            }
        }
    }

    @Override
    public void agregarDonante(DonanteResponse donante, Long idUsuarioAuth) {
        String crearDonantePath = "/donante";
        String uri = this.incentivosServiceUri + crearDonantePath;
        int intentosActuales = 0;
        long tiempoDeEspera = 1000;

        List<MedioDeContactoRequest> contactosList = List.of();
        if (donante.contactos() != null) {
            contactosList = donante.contactos().stream()
                    .map(c -> new MedioDeContactoRequest(
                            c.tipo() != null ? c.tipo().name() : null,
                            c.valor()
                    ))
                    .toList();
        }

        CrearDonanteRequest donanteDto = new CrearDonanteRequest(donante.id(), idUsuarioAuth,donante.nombre(), contactosList);
        log.info("[IncentivosAdapter] Enviando donante a Incentivos: idDonante={}, uri={}", donante.id(), uri);
        while (intentosActuales < intentosMaximos) {
            try {
                restApiCallerService.post(uri, donanteDto, Void.class);
                log.info("[IncentivosAdapter] Donante enviado con exito a Incentivos: idDonante={}", donante.id());
                break;
            } catch (HttpServerErrorException e) {
                log.warn("[IncentivosAdapter] Error 500 al enviar donante (intento {}/{}): {}", intentosActuales + 1, intentosMaximos, e.getMessage());
                intentosActuales++;
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
                log.error("[IncentivosAdapter] Error 4xx al enviar donante a Incentivos: {}", e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("[IncentivosAdapter] Error de comunicación con Incentivos Service: {}", e.getMessage());
                break;
            }
        }
    }
}

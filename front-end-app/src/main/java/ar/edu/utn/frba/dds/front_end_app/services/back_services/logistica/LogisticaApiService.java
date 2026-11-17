package ar.edu.utn.frba.dds.front_end_app.services.back_services.logistica;

import ar.edu.utn.frba.dds.front_end_app.dto.logistica.ActualizarEstadoEntregaRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.ActualizarEstadoRutaRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.CamionRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.CamionResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.EntregaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.ParadaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.PesajeDonacionRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.RutaResponse;
import ar.edu.utn.frba.dds.front_end_app.services.DonacionService;
import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogisticaApiService {

    private static final Logger log = LoggerFactory.getLogger(LogisticaApiService.class);
    private final String logisticaServiceUrl;
    private final DonacionService donacionService;
    private final WebApiCallerService webApiCallerService;

    public LogisticaApiService(
        @Value("${logistica.service.url:http://localhost:8083/logistica-service}") String logisticaServiceUrl, DonacionService donacionService,
        WebApiCallerService webApiCallerService) {
        this.logisticaServiceUrl = logisticaServiceUrl;
      this.donacionService = donacionService;
      this.webApiCallerService = webApiCallerService;
    }

    public List<CamionResponse> obtenerTodosLosCamionesConDonaciones() {
        try {
            String url = logisticaServiceUrl + "/camiones";
            log.info("Llamando a logistica-service usando WebApiCallerService: {}", url);

            List<CamionResponse> response = webApiCallerService.getList(url, CamionResponse.class);
            if(response != null) {
                response.forEach(c -> {
                    if (c.getRutaActiva() != null) {
                        c.getRutaActiva().getParadas().forEach(p -> {
                            p.getEntrega()
                                .setDonaciones(p.getEntrega().getDonacionesIds().stream()
                                    .map(donacionService::obtenerDonacion).toList());
                        });
                    }
                });
                return response;
            }
            return List.of();
        } catch (Exception e) {
            log.error("Error al obtener camiones de {}: {}", logisticaServiceUrl, e.getMessage());
            return List.of();
        }
    }

    public void agregarCamion(CamionRequest request) {
        String url = logisticaServiceUrl + "/camiones";
        log.info("Creando camión en logistica-service: {}", url);
        webApiCallerService.post(url, request, CamionResponse.class);
    }

    public void eliminarCamion(String id) {
        String url = logisticaServiceUrl + "/camiones/" + id;
        log.info("Eliminando camión en logistica-service: {}", url);
        webApiCallerService.delete(url);
    }

    public void eliminarCamion(Long id) {
        if (id != null) {
            eliminarCamion(id.toString());
        }
    }

    public void iniciarViaje(String camionId) {
        String urlEstado = logisticaServiceUrl + "/camiones/" + camionId + "/estado?estaEnDeposito=false";
        log.info("Iniciando viaje (camión {} sale del depósito): {}", camionId, urlEstado);
        webApiCallerService.patch(urlEstado, CamionResponse.class);
    }

    public void iniciarViaje(Long camionId) {
        if (camionId != null) {
            iniciarViaje(camionId.toString());
        }
    }

    public void finalizarViaje(String camionId) {
        String urlEstado = logisticaServiceUrl + "/camiones/" + camionId + "/estado?estaEnDeposito=true";
        log.info("Finalizando viaje (camión {} vuelve al depósito): {}", camionId, urlEstado);
        webApiCallerService.patch(urlEstado, CamionResponse.class);
    }

    public CamionResponse buscarCamionPorId(String id) {
        try {
            String url = logisticaServiceUrl + "/camiones/" + id;
            return webApiCallerService.get(url, CamionResponse.class);
        } catch (Exception e) {
            log.error("Error al buscar camión ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public CamionResponse obtenerCamionPorUsuario(Long idUsuario) {
        try {
            String url = logisticaServiceUrl + "/camiones/usuario/" + idUsuario;
            log.info("Llamando a logistica-service para obtener camión del usuario {}: {}", idUsuario, url);
            return webApiCallerService.get(url, CamionResponse.class);
        } catch (Exception e) {
            log.error("Error al obtener camión para el usuario {}: {}", idUsuario, e.getMessage());
            return null;
        }
    }

    public List<RutaResponse> obtenerRutas() {
        try {
            String url = logisticaServiceUrl + "/rutas";
            List<RutaResponse> rutas = webApiCallerService.getList(url, RutaResponse.class);
            return rutas != null ? rutas : List.of();
        } catch (Exception e) {
            log.error("Error al obtener rutas: {}", e.getMessage());
            return List.of();
        }
    }

    public List<ParadaResponse> obtenerParadasDelCamion(CamionResponse camion) {
        if (camion == null) return List.of();
        try {
            if (camion.getRutasIds() != null && !camion.getRutasIds().isEmpty()) {
                // Tomar la última ruta asignada al camión
                Long ultimaRutaId = camion.getRutasIds().get(camion.getRutasIds().size() - 1);
                String url = logisticaServiceUrl + "/rutas/" + ultimaRutaId;
                RutaResponse ruta = webApiCallerService.get(url, RutaResponse.class);
                if (ruta != null && ruta.getParadas() != null) {
                    return ruta.getParadas();
                }
            }
        } catch (Exception e) {
            log.error("Error al obtener paradas del camión {}: {}", camion.getId(), e.getMessage());
        }
        return List.of();
    }

    public boolean tieneRutaFinalizada(CamionResponse camion) {
        if (camion == null || camion.getRutasIds() == null || camion.getRutasIds().isEmpty()) return false;
        try {
            Long ultimaRutaId = camion.getRutasIds().get(camion.getRutasIds().size() - 1);
            String url = logisticaServiceUrl + "/rutas/" + ultimaRutaId;
            RutaResponse ruta = webApiCallerService.get(url, RutaResponse.class);
            if (ruta != null && "VIAJE_FINALIZADO".equalsIgnoreCase(ruta.getEstadoActual())) {
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    public void confirmarEntregaExitosaPorDonacion(Long donacionId) {
        if (donacionId == null) return;
        try {
            List<RutaResponse> rutas = obtenerRutas();
            for (RutaResponse ruta : rutas) {
                if (ruta.getParadas() != null) {
                    for (ParadaResponse parada : ruta.getParadas()) {
                        if (parada.getEntrega() != null && parada.getEntrega().getDonacionesIds() != null
                                && parada.getEntrega().getDonacionesIds().contains(donacionId)) {
                            confirmarEntregaExitosa(parada.getEntrega().getId());
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo buscar entrega por donacion {} en logistica-service: {}", donacionId, e.getMessage());
        }
    }

    public void cancelarEntrega(Long entregaId, String motivo) {
        String url = logisticaServiceUrl + "/entregas/" + entregaId + "/estado";
        log.info("Cancelando entrega {} con motivo: {}", entregaId, motivo);
        ActualizarEstadoEntregaRequest request = new ActualizarEstadoEntregaRequest(
                "NO_RECIBIDA",
                motivo,
                LocalDate.now()
        );
        webApiCallerService.patch(url, request, EntregaResponse.class);
    }

    public void confirmarEntregaExitosa(Long entregaId) {
        if (entregaId == null) return;
        try {
            String url = logisticaServiceUrl + "/entregas/" + entregaId + "/estado";
            log.info("Marcando entrega {} como EXITOSA en logistica-service", entregaId);
            ActualizarEstadoEntregaRequest request = new ActualizarEstadoEntregaRequest(
                    "EXITOSA",
                    "Entrega confirmada y recibida por la entidad con foto de constancia",
                    LocalDate.now()
            );
            webApiCallerService.patch(url, request, EntregaResponse.class);
        } catch (Exception e) {
            log.warn("No se pudo actualizar entrega {} en logistica-service: {}", entregaId, e.getMessage());
        }
    }

    public void registrarPesaje(Long donacionId, Double peso, Double volumen) {
        String url = logisticaServiceUrl + "/recepcion/pesaje-donacion/" + donacionId;
        log.info("Registrando pesaje para donacion {} en logistica-service: peso={}, volumen={}", donacionId, peso, volumen);
        PesajeDonacionRequest request = new PesajeDonacionRequest(peso, volumen);
        webApiCallerService.post(url, request, String.class);
    }
}

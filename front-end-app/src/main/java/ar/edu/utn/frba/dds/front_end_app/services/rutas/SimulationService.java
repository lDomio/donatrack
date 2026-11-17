package ar.edu.utn.frba.dds.front_end_app.services.rutas;

import ar.edu.utn.frba.dds.front_end_app.dto.logistica.ParadaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.rutas.ParadaSimulada;
import ar.edu.utn.frba.dds.front_end_app.dto.rutas.RouteResponseDto;
import ar.edu.utn.frba.dds.front_end_app.dto.rutas.TruckPositionDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimulationService {

    private static class SimulacionCamion {
        Long activeRutaId = 125L;
        Long camionId = 101L;
        RouteResponseDto cachedRoute = null;
        List<double[]> currentGeometry = new ArrayList<>();
        int currentIndex = 0;
        double totalDistanciaKm = 0.0;
        double totalDuracionMin = 0.0;
        boolean enCurso = false;
        List<ParadaSimulada> paradasSimuladas = new ArrayList<>();
        int currentParadaIndex = 0;
        boolean pausadoEnParada = false;
    }

    private final Map<String, SimulacionCamion> simulaciones = new LinkedHashMap<>();

    public synchronized RouteResponseDto asegurarRutaCargada(RouteResponseDto nuevaRuta, boolean activarSiHayViaje) {
        if (nuevaRuta == null || nuevaRuta.getGeometry() == null || nuevaRuta.getGeometry().isEmpty()) {
            return nuevaRuta;
        }
        double[] ultimoPunto = nuevaRuta.getGeometry().get(nuevaRuta.getGeometry().size() - 1);
        SimulacionCamion existente = buscarSimulacionPorDestino(ultimoPunto[0], ultimoPunto[1]);
        if (existente != null && !existente.currentGeometry.isEmpty()) {
            RouteResponseDto dto = new RouteResponseDto();
            dto.setRutaId(existente.activeRutaId);
            dto.setDistanciaKm(existente.totalDistanciaKm);
            dto.setDuracionMin(existente.totalDuracionMin);
            dto.setGeometry(existente.currentGeometry);
            dto.setStatus("OK");
            return dto;
        }
        return nuevaRuta;
    }

    public synchronized void iniciarSimulacion(RouteResponseDto route) {
        iniciarSimulacion("default", route, List.of());
    }

    public synchronized void iniciarSimulacion(RouteResponseDto route, List<ParadaResponse> paradas) {
        iniciarSimulacion("default", route, paradas);
    }

    public synchronized void iniciarSimulacion(String camionKey, RouteResponseDto route, List<ParadaResponse> paradas) {
        if (route == null || route.getGeometry() == null || route.getGeometry().isEmpty()) {
            return;
        }
        String key = (camionKey != null && !camionKey.isBlank()) ? camionKey : "default";
        SimulacionCamion sim = new SimulacionCamion();
        sim.cachedRoute = route;
        sim.activeRutaId = route.getRutaId() != null ? route.getRutaId() : (long) Math.abs(key.hashCode() % 1000);
        sim.camionId = (long) Math.abs(key.hashCode() % 1000);
        sim.currentGeometry = route.getGeometry();
        sim.totalDistanciaKm = route.getDistanciaKm();
        sim.totalDuracionMin = route.getDuracionMin();
        sim.currentIndex = 0;
        sim.enCurso = true;
        sim.pausadoEnParada = false;
        sim.currentParadaIndex = 0;
        sim.paradasSimuladas = mapearParadas(paradas, sim.currentGeometry);
        simulaciones.put(key, sim);
    }

    private List<ParadaSimulada> mapearParadas(List<ParadaResponse> paradas, List<double[]> geometry) {
        List<ParadaSimulada> resultado = new ArrayList<>();
        if (paradas == null || paradas.isEmpty() || geometry == null || geometry.isEmpty()) {
            return resultado;
        }

        for (ParadaResponse p : paradas) {
            if (p.getEntrega() != null && p.getEntrega().getLatitud() != null && p.getEntrega().getLongitud() != null) {
                double targetLat = p.getEntrega().getLatitud();
                double targetLng = p.getEntrega().getLongitud();

                int bestIdx = 0;
                double bestDist = Double.MAX_VALUE;
                for (int i = 0; i < geometry.size(); i++) {
                    double[] pt = geometry.get(i);
                    double dLat = pt[0] - targetLat;
                    double dLng = pt[1] - targetLng;
                    double dist = dLat * dLat + dLng * dLng;
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestIdx = i;
                    }
                }

                String direccion = p.getEntrega().getDireccionEntidad() != null
                        ? p.getEntrega().getDireccionEntidad()
                        : "Parada #" + p.getNumeroDeParada();

                ParadaSimulada ps = ParadaSimulada.builder()
                        .idParada(p.getId())
                        .idEntrega(p.getEntrega().getId())
                        .numeroParada(p.getNumeroDeParada())
                        .nombreDestino(direccion)
                        .latitud(targetLat)
                        .longitud(targetLng)
                        .geometryIndex(bestIdx)
                        .donacionesIds(p.getEntrega().getDonacionesIds() != null ? p.getEntrega().getDonacionesIds() : List.of())
                        .completada(false)
                        .cancelada(false)
                        .build();

                resultado.add(ps);
            }
        }

        resultado.sort(Comparator.comparingInt(ParadaSimulada::getGeometryIndex));
        return resultado;
    }

    public synchronized void detenerSimulacion() {
        for (SimulacionCamion sim : simulaciones.values()) {
            sim.enCurso = false;
            sim.pausadoEnParada = false;
        }
    }

    public synchronized void detenerSimulacion(String camionKey) {
        if (camionKey != null && simulaciones.containsKey(camionKey)) {
            SimulacionCamion sim = simulaciones.get(camionKey);
            sim.enCurso = false;
            sim.pausadoEnParada = false;
        } else {
            detenerSimulacion();
        }
    }

    public synchronized void marcarEntregado() {
        for (SimulacionCamion sim : simulaciones.values()) {
            if (!sim.currentGeometry.isEmpty()) {
                sim.currentIndex = sim.currentGeometry.size() - 1;
            }
            sim.enCurso = false;
            sim.pausadoEnParada = false;
        }
    }

    public synchronized boolean confirmarEntregaParada(Long donacionId) {
        if (donacionId != null) {
            for (SimulacionCamion sim : simulaciones.values()) {
                for (int i = 0; i < sim.paradasSimuladas.size(); i++) {
                    ParadaSimulada p = sim.paradasSimuladas.get(i);
                    if (p.getDonacionesIds() != null && p.getDonacionesIds().contains(donacionId)) {
                        p.setCompletada(true);
                        if (i == sim.currentParadaIndex && sim.pausadoEnParada) {
                            sim.pausadoEnParada = false;
                            sim.currentParadaIndex++;
                            if (sim.currentParadaIndex >= sim.paradasSimuladas.size() && sim.currentIndex >= sim.currentGeometry.size() - 1) {
                                sim.enCurso = false;
                            }
                            return true;
                        }
                    }
                }
            }
        }

        for (SimulacionCamion sim : simulaciones.values()) {
            if (sim.pausadoEnParada && sim.currentParadaIndex < sim.paradasSimuladas.size()) {
                ParadaSimulada p = sim.paradasSimuladas.get(sim.currentParadaIndex);
                p.setCompletada(true);
                sim.pausadoEnParada = false;
                sim.currentParadaIndex++;
                if (sim.currentParadaIndex >= sim.paradasSimuladas.size() && sim.currentIndex >= sim.currentGeometry.size() - 1) {
                    sim.enCurso = false;
                }
                return true;
            }
        }
        return false;
    }

    public synchronized boolean cancelarEntregaParada(Long idEntrega) {
        if (idEntrega != null) {
            for (SimulacionCamion sim : simulaciones.values()) {
                for (int i = 0; i < sim.paradasSimuladas.size(); i++) {
                    ParadaSimulada p = sim.paradasSimuladas.get(i);
                    if (p.getIdEntrega() != null && p.getIdEntrega().equals(idEntrega)) {
                        p.setCancelada(true);
                        if (i == sim.currentParadaIndex && sim.pausadoEnParada) {
                            sim.pausadoEnParada = false;
                            sim.currentParadaIndex++;
                            if (sim.currentParadaIndex >= sim.paradasSimuladas.size() && sim.currentIndex >= sim.currentGeometry.size() - 1) {
                                sim.enCurso = false;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private SimulacionCamion buscarSimulacionPorDestino(Double destinoLat, Double destinoLng) {
        if (simulaciones.isEmpty()) {
            return null;
        }
        if (destinoLat != null && destinoLng != null) {
            // 1. Buscar coincidencia exacta o cercana en paradasSimuladas
            SimulacionCamion mejorMatch = null;
            double menorDist = 0.01; // tolerancia ~1km
            for (SimulacionCamion sim : simulaciones.values()) {
                for (ParadaSimulada p : sim.paradasSimuladas) {
                    double d = Math.hypot(p.getLatitud() - destinoLat, p.getLongitud() - destinoLng);
                    if (d < menorDist) {
                        menorDist = d;
                        mejorMatch = sim;
                    }
                }
            }
            if (mejorMatch != null) {
                return mejorMatch;
            }
            // 2. Fallback por último punto de la geometría
            for (SimulacionCamion sim : simulaciones.values()) {
                if (!sim.currentGeometry.isEmpty()) {
                    double[] end = sim.currentGeometry.get(sim.currentGeometry.size() - 1);
                    double d = Math.hypot(end[0] - destinoLat, end[1] - destinoLng);
                    if (d < 0.015) {
                        return sim;
                    }
                }
            }
        }
        // Fallback: devolver la última simulación activa o registrada
        SimulacionCamion ultima = null;
        for (SimulacionCamion sim : simulaciones.values()) {
            ultima = sim;
            if (sim.enCurso) {
                return sim;
            }
        }
        return ultima;
    }

    public synchronized TruckPositionDto getPosicionActual() {
        return getPosicionActual(null, null);
    }

    public synchronized TruckPositionDto getPosicionActual(Double destinoLat, Double destinoLng) {
        SimulacionCamion sim = buscarSimulacionPorDestino(destinoLat, destinoLng);
        if (sim == null || sim.currentGeometry.isEmpty()) {
            return null;
        }
        return construirPosicion(sim, sim.currentIndex);
    }

    public synchronized List<TruckPositionDto> avanzarTodasLasSimulaciones() {
        List<TruckPositionDto> posiciones = new ArrayList<>();
        for (SimulacionCamion sim : simulaciones.values()) {
            TruckPositionDto pos = avanzarSimulacion(sim);
            if (pos != null) {
                posiciones.add(pos);
            }
        }
        return posiciones;
    }

    public synchronized TruckPositionDto getNextPosition() {
        List<TruckPositionDto> lista = avanzarTodasLasSimulaciones();
        return lista.isEmpty() ? null : lista.get(lista.size() - 1);
    }

    private TruckPositionDto avanzarSimulacion(SimulacionCamion sim) {
        if (sim.currentGeometry.isEmpty() || !sim.enCurso) {
            return null;
        }

        if (sim.pausadoEnParada) {
            return construirPosicion(sim, sim.currentIndex);
        }

        int targetIndex = sim.currentGeometry.size() - 1;
        ParadaSimulada siguienteParada = null;
        if (sim.currentParadaIndex < sim.paradasSimuladas.size()) {
            siguienteParada = sim.paradasSimuladas.get(sim.currentParadaIndex);
            targetIndex = siguienteParada.getGeometryIndex();
        }

        if (sim.currentIndex < targetIndex) {
            sim.currentIndex = Math.min(sim.currentIndex + 3, targetIndex);
        }

        if (siguienteParada != null && sim.currentIndex >= targetIndex) {
            sim.currentIndex = targetIndex;
            sim.pausadoEnParada = true;
        } else if (siguienteParada == null && sim.currentIndex >= sim.currentGeometry.size() - 1) {
            sim.currentIndex = sim.currentGeometry.size() - 1;
            sim.enCurso = false;
        }

        return construirPosicion(sim, sim.currentIndex);
    }

    private TruckPositionDto construirPosicion(SimulacionCamion sim, int index) {
        double[] point = sim.currentGeometry.get(index);
        double lat = point[0];
        double lng = point[1];

        double porcentaje = sim.currentGeometry.size() > 1
                ? Math.round(((double) index / (sim.currentGeometry.size() - 1)) * 100.0 * 10.0) / 10.0
                : 100.0;

        double distanciaRestanteKm = Math.round((sim.totalDistanciaKm * (1.0 - (porcentaje / 100.0))) * 100.0) / 100.0;
        double tiempoRestanteMin = Math.round(sim.totalDuracionMin * (1.0 - (porcentaje / 100.0)));

        String estado;
        Long paradaId = null;
        Integer numParada = null;
        String nombreParada = null;

        if (sim.pausadoEnParada && sim.currentParadaIndex < sim.paradasSimuladas.size()) {
            estado = "EN_PARADA";
            ParadaSimulada p = sim.paradasSimuladas.get(sim.currentParadaIndex);
            paradaId = p.getIdParada();
            numParada = p.getNumeroParada();
            nombreParada = p.getNombreDestino();
            tiempoRestanteMin = 0.0;
        } else if (!sim.enCurso && index == 0) {
            estado = "EN_DEPOSITO";
        } else if (index >= sim.currentGeometry.size() - 1 && (!sim.pausadoEnParada && sim.currentParadaIndex >= sim.paradasSimuladas.size())) {
            estado = "DESTINO_ALCANZADO";
        } else {
            estado = "EN_CAMINO";
            if (sim.currentParadaIndex < sim.paradasSimuladas.size()) {
                ParadaSimulada p = sim.paradasSimuladas.get(sim.currentParadaIndex);
                paradaId = p.getIdParada();
                numParada = p.getNumeroParada();
                nombreParada = p.getNombreDestino();
            }
        }

        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        TruckPositionDto pos = new TruckPositionDto(
                sim.camionId,
                sim.activeRutaId,
                lat,
                lng,
                porcentaje,
                (sim.enCurso && !sim.pausadoEnParada) ? 45.0 : 0.0,
                distanciaRestanteKm,
                tiempoRestanteMin,
                estado,
                timestamp
        );
        pos.setParadaActualId(paradaId);
        pos.setNumeroParadaActual(numParada);
        pos.setNombreParadaActual(nombreParada);
        pos.setEnParada(sim.pausadoEnParada);

        return pos;
    }

    public synchronized boolean isPausadoEnParada() {
        return simulaciones.values().stream().anyMatch(s -> s.pausadoEnParada);
    }

    public synchronized boolean hasActiveSimulation() {
        return simulaciones.values().stream().anyMatch(s -> !s.currentGeometry.isEmpty() && s.enCurso);
    }
}

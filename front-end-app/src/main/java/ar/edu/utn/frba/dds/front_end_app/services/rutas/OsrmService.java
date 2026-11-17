package ar.edu.utn.frba.dds.front_end_app.services.rutas;

import ar.edu.utn.frba.dds.front_end_app.dto.rutas.RouteResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OsrmService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AtomicLong rutaIdGenerator = new AtomicLong(100);

    public OsrmService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public RouteResponseDto calcularRuta(double origenLat, double origenLng, double destinoLat, double destinoLng) {
        String url = String.format(Locale.US,
                "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                origenLng, origenLat, destinoLng, destinoLat
        );

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response);

            if (!rootNode.has("routes") || rootNode.get("routes").isEmpty()) {
                return new RouteResponseDto(null, 0, 0, List.of(), "ERROR", "No se encontró ruta entre los puntos especificados.");
            }

            JsonNode routeNode = rootNode.get("routes").get(0);
            double distanceMeters = routeNode.get("distance").asDouble();
            double durationSeconds = routeNode.get("duration").asDouble();

            double distanciaKm = Math.round((distanceMeters / 1000.0) * 100.0) / 100.0;
            double duracionMin = Math.max(1, Math.round(durationSeconds / 60.0));

            JsonNode coordinatesNode = routeNode.get("geometry").get("coordinates");
            List<double[]> geometry = new ArrayList<>();

            if (coordinatesNode.isArray()) {
                for (JsonNode coord : coordinatesNode) {
                    double lng = coord.get(0).asDouble();
                    double lat = coord.get(1).asDouble();
                    geometry.add(new double[]{lat, lng});
                }
            }

            Long rutaId = rutaIdGenerator.incrementAndGet();
            return new RouteResponseDto(rutaId, distanciaKm, duracionMin, geometry, "OK", "Ruta calculada con éxito por OSRM");

        } catch (Exception e) {
            return generarRutaFallbackInterpolada(origenLat, origenLng, destinoLat, destinoLng);
        }
    }

    public RouteResponseDto calcularRutaPuntos(List<double[]> waypoints) {
        if (waypoints == null || waypoints.size() < 2) {
            return calcularRuta(-34.598500, -58.420100, -34.617500, -58.367500);
        }
        if (waypoints.size() == 2) {
            return calcularRuta(waypoints.get(0)[0], waypoints.get(0)[1], waypoints.get(1)[0], waypoints.get(1)[1]);
        }

        StringBuilder coordsBuilder = new StringBuilder();
        for (int i = 0; i < waypoints.size(); i++) {
            if (i > 0) coordsBuilder.append(";");
            double[] wp = waypoints.get(i);
            coordsBuilder.append(String.format(Locale.US, "%.6f,%.6f", wp[1], wp[0]));
        }

        String url = "https://router.project-osrm.org/route/v1/driving/" + coordsBuilder + "?overview=full&geometries=geojson";
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            if (!rootNode.has("routes") || rootNode.get("routes").isEmpty()) {
                return fallbackMultiPuntos(waypoints);
            }
            JsonNode routeNode = rootNode.get("routes").get(0);
            double distanceMeters = routeNode.get("distance").asDouble();
            double durationSeconds = routeNode.get("duration").asDouble();
            double distanciaKm = Math.round((distanceMeters / 1000.0) * 100.0) / 100.0;
            double duracionMin = Math.max(1, Math.round(durationSeconds / 60.0));

            JsonNode coordinatesNode = routeNode.get("geometry").get("coordinates");
            List<double[]> geometry = new ArrayList<>();
            if (coordinatesNode.isArray()) {
                for (JsonNode coord : coordinatesNode) {
                    double lng = coord.get(0).asDouble();
                    double lat = coord.get(1).asDouble();
                    geometry.add(new double[]{lat, lng});
                }
            }
            Long rutaId = rutaIdGenerator.incrementAndGet();
            return new RouteResponseDto(rutaId, distanciaKm, duracionMin, geometry, "OK", "Ruta calculada con éxito por OSRM");
        } catch (Exception e) {
            return fallbackMultiPuntos(waypoints);
        }
    }

    private RouteResponseDto fallbackMultiPuntos(List<double[]> waypoints) {
        List<double[]> geometry = new ArrayList<>();
        double distanciaTotal = 0;
        int stepsPorTramo = 30;
        for (int w = 0; w < waypoints.size() - 1; w++) {
            double[] origen = waypoints.get(w);
            double[] destino = waypoints.get(w + 1);
            for (int i = (w == 0 ? 0 : 1); i <= stepsPorTramo; i++) {
                double t = (double) i / stepsPorTramo;
                double lat = origen[0] + (destino[0] - origen[0]) * t;
                double lng = origen[1] + (destino[1] - origen[1]) * t;
                geometry.add(new double[]{lat, lng});
            }
            distanciaTotal += 5.5;
        }
        Long rutaId = rutaIdGenerator.incrementAndGet();
        return new RouteResponseDto(rutaId, Math.round(distanciaTotal * 10.0) / 10.0, 15.0, geometry, "OK", "Ruta simulada multi-parada activa");
    }

    private RouteResponseDto generarRutaFallbackInterpolada(double origenLat, double origenLng, double destinoLat, double destinoLng) {
        List<double[]> geometry = new ArrayList<>();
        int steps = 40;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double lat = origenLat + (destinoLat - origenLat) * t;
            double lng = origenLng + (destinoLng - origenLng) * t;
            geometry.add(new double[]{lat, lng});
        }
        Long rutaId = rutaIdGenerator.incrementAndGet();
        return new RouteResponseDto(rutaId, 3.8, 14.0, geometry, "OK", "Ruta simulada activa");
    }
}

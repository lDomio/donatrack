package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.rutas.RouteRequestDto;
import ar.edu.utn.frba.dds.front_end_app.dto.rutas.RouteResponseDto;
import ar.edu.utn.frba.dds.front_end_app.dto.rutas.TruckPositionDto;
import ar.edu.utn.frba.dds.front_end_app.services.rutas.OsrmService;
import ar.edu.utn.frba.dds.front_end_app.services.rutas.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rutas")
public class RouteController {

    private final OsrmService osrmService;
    private final SimulationService simulationService;

    public RouteController(OsrmService osrmService, SimulationService simulationService) {
        this.osrmService = osrmService;
        this.simulationService = simulationService;
    }

    @PostMapping("/calcular")
    public ResponseEntity<RouteResponseDto> calcularRuta(
            @RequestBody RouteRequestDto request,
            @RequestParam(defaultValue = "false") boolean iniciarSiActivo) {
        RouteResponseDto response = osrmService.calcularRuta(
                request.getOrigenLat(),
                request.getOrigenLng(),
                request.getDestinoLat(),
                request.getDestinoLng()
        );

        if ("OK".equals(response.getStatus())) {
            response = simulationService.asegurarRutaCargada(response, iniciarSiActivo);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/posicion-actual")
    public ResponseEntity<TruckPositionDto> obtenerPosicionActual(
            @RequestParam(required = false) Double destinoLat,
            @RequestParam(required = false) Double destinoLng) {
        TruckPositionDto pos = simulationService.getPosicionActual(destinoLat, destinoLng);
        return pos != null ? ResponseEntity.ok(pos) : ResponseEntity.noContent().build();
    }
}

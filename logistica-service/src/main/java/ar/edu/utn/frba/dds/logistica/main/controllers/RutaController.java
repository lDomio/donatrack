package ar.edu.utn.frba.dds.logistica.main.controllers;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.ActualizarEstadoRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.RutaResponse;
import ar.edu.utn.frba.dds.logistica.main.services.CamionService;
import ar.edu.utn.frba.dds.logistica.main.services.RutaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping({"/logistica-service/rutas", "/logisticaService/rutas"})
public class RutaController {
    private final RutaService service;
    private final CamionService camionService;

    public RutaController(RutaService service, CamionService camionService) {
        this.service = service;
      this.camionService = camionService;
    }

    @PostMapping({"/confirmar", "/confirmar/"})
    public ResponseEntity<String> confirmarRutas(@RequestBody RoutingResult request) {
        service.procesarResultadoRouting(request);
        return ResponseEntity.ok("Planificación de rutas procesada correctamente");
    }

    @GetMapping({"", "/"})
    public List<RutaResponse> obtenerTodas() {
        return service.obtenerTodas();
    }

    @GetMapping({"/{id}", "/{id}/"})
    @PreAuthorize("hasRole('CAMIONERO') and hasAuthority('VER_MI_RUTA')")
    public RutaResponse buscarPorId(@PathVariable Long id, Authentication authentication) {
        camionService.validarRutaDeCamion(((Usuario) authentication.getPrincipal()).idUsuarioAuth(), id);
        return service.buscarPorId(id);
    }

    @PatchMapping({"/{id}/estado", "/{id}/estado/"})
    public RutaResponse actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoRutaRequest request) {
        return service.actualizarEstado(id, request);
    }

    @PatchMapping({"/{id}/iniciar-ruta", "{id}/iniciar-ruta", "/{id}/iniciar-ruta/", "{id}/iniciar-ruta/"})
    public RutaResponse iniciarRuta(@PathVariable Long id, @RequestBody(required = false) String responsableDelCambio) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("El id no puede ser nulo o cero");
        }

        if (responsableDelCambio == null || Objects.equals(responsableDelCambio, "null") || responsableDelCambio.isBlank()) {
            responsableDelCambio = "Administrador";
        }
        ActualizarEstadoRutaRequest dto = new ActualizarEstadoRutaRequest("VIAJE_COMENZADO", responsableDelCambio);
        return service.actualizarEstado(id, dto);
    }
}

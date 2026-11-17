package ar.edu.utn.frba.dds.logistica.main.controllers;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.ActualizarEstadoEntregaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.EntregaResponse;
import ar.edu.utn.frba.dds.logistica.main.services.CamionService;
import ar.edu.utn.frba.dds.logistica.main.services.EntregaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/logistica-service/entregas")
public class EntregaController {
    private final EntregaService entregaService;
    private final CamionService camionService;

    public EntregaController(EntregaService entregaService, CamionService camionService) {
        this.entregaService = entregaService;
      this.camionService = camionService;
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("(hasRole('CAMIONERO') and hasAuthority('CANCELAR_MI_ENTREGA')) or (hasRole('ENTIDAD_BENEFICIARIA') and hasAuthority('CONFIRMAR_RECEPCION_DE_DONACION_ASIGNADA'))")
    public EntregaResponse actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoEntregaRequest request, Authentication authentication) {
        if ("CANCELADA".equalsIgnoreCase(request.nuevoEstado())) {
            camionService.validarSiCamionPuedeCancelarEntrega(((Usuario) authentication.getPrincipal()).idUsuarioAuth(), id, request);
        }
        return entregaService.actualizarEstado(id, request);
    }

    @GetMapping("/pendientes")
    public List<EntregaResponse> listarPendientes() {
        return entregaService.listarPendientes();
    }

    @GetMapping("/{id}")
    public EntregaResponse buscarPorId(@PathVariable Long id) {
        return entregaService.buscarPorId(id);
    }
}

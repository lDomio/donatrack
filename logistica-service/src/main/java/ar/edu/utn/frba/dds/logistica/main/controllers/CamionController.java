package ar.edu.utn.frba.dds.logistica.main.controllers;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.logistica.main.exception.UnauthorizedException;
import ar.edu.utn.frba.dds.logistica.main.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.logistica.main.dtos.camion.CamionRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.camion.CamionResponse;
import ar.edu.utn.frba.dds.logistica.main.services.CamionService;
import java.util.Objects;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/logistica-service/camiones", "/logisticaService/camiones"})
public class CamionController {
    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('GESTIONAR_CAMIONES')")
    public List<CamionResponse> obtenerTodosLosCamiones() {
        return camionService.listarTodos();
    }

    @GetMapping({"/{id}", "/{id}/"})
    public CamionResponse buscarPorId(@PathVariable String id) {
        return camionService.buscarPorId(id);
    }

    @GetMapping({"/usuario/{idUsuario}", "/usuario/{idUsuario}/"})
    @PreAuthorize("hasRole('CAMIONERO') and hasAuthority('VER_MI_CAMION')")
    public CamionResponse buscarPorIdUsuario(@PathVariable Long idUsuario, Authentication authentication) {
        if(!Objects.equals(((Usuario) authentication.getPrincipal()).idUsuarioAuth(), idUsuario)){
            throw new UnauthorizedException("Un camionero solo puede acceder a su camion");
        }
        return camionService.buscarPorIdUsuario(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró camión asignado al usuario ID: " + idUsuario));
    }

    @PostMapping({"", "/"})
    @PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('GESTIONAR_CAMIONES')")
    public CamionResponse agregarCamion(@RequestBody CamionRequest request) {
        return camionService.agregarCamion(request);
    }

    @PatchMapping({"/{id}/chofer", "/{id}/chofer/"})
    public CamionResponse modificarChofer(@PathVariable String id, @RequestBody String nuevoChofer) {
        return camionService.modificarChofer(id, nuevoChofer);
    }

    @PatchMapping({"/{id}/estado", "/{id}/estado/"})
    @PreAuthorize("hasRole('CAMIONERO') and hasAuthority('MODIFICAR_ESTADO_CAMION')")
    public CamionResponse modificarEstado(@PathVariable String id, @RequestParam(required = false, defaultValue = "false") Boolean estaEnDeposito, Authentication authentication) {
        camionService.validarUsuarioDeCamion(((Usuario)authentication.getPrincipal()).idUsuarioAuth(), id);
        return camionService.modificarEstado(id, estaEnDeposito);
    }

    @PatchMapping({"/{id}/iniciar-viaje", "/{id}/iniciar-viaje/"})
    @PreAuthorize("hasRole('CAMIONERO') and hasAuthority('INICIAR_VIAJE')")
    public CamionResponse iniciarViaje(@PathVariable String id, Authentication authentication) {
        camionService.validarUsuarioDeCamion(((Usuario)authentication.getPrincipal()).idUsuarioAuth(), id);
        return camionService.iniciarViaje(id);
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    @PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('GESTIONAR_CAMIONES')")
    public void eliminarCamion(@PathVariable String id) {
        camionService.eliminarCamion(id);
    }
}

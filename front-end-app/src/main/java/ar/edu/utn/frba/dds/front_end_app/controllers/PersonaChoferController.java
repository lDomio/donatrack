package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.logistica.CamionResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.ParadaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.rutas.RouteResponseDto;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.logistica.LogisticaApiService;
import ar.edu.utn.frba.dds.front_end_app.services.rutas.OsrmService;
import ar.edu.utn.frba.dds.front_end_app.services.rutas.SimulationService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/personaChofer")
@PreAuthorize("hasRole('CAMIONERO')")
public class PersonaChoferController {

    private static final Logger log = LoggerFactory.getLogger(PersonaChoferController.class);
    private final LogisticaApiService logisticaApiService;
    private final OsrmService osrmService;
    private final SimulationService simulationService;

    public PersonaChoferController(
            LogisticaApiService logisticaApiService,
            OsrmService osrmService,
            SimulationService simulationService) {
        this.logisticaApiService = logisticaApiService;
        this.osrmService = osrmService;
        this.simulationService = simulationService;
    }

    @GetMapping({"", "/", "/miRuta"})
    @PreAuthorize("hasAuthority('VER_MI_CAMION') and hasAuthority('VER_MI_RUTA')")
    public String miRuta(HttpSession session, Model model) {
        Object idUsuarioObj = session.getAttribute("idUsuarioAuth");
        Long idUsuario = null;
        if (idUsuarioObj instanceof Number num) {
            idUsuario = num.longValue();
        } else if (idUsuarioObj instanceof String str) {
            try {
                idUsuario = Long.parseLong(str);
            } catch (NumberFormatException ignored) {}
        }

        CamionResponse camion = null;
        List<ParadaResponse> paradas = List.of();
        boolean rutaFinalizada = false;
        if (idUsuario != null) {
            camion = logisticaApiService.obtenerCamionPorUsuario(idUsuario);
        }
        if (camion == null) {
            // Fallback para chofer de flota asignado a unidad principal
            camion = logisticaApiService.buscarCamionPorId("ABC-123");
            if (camion == null) {
                List<CamionResponse> todos = logisticaApiService.obtenerTodosLosCamionesConDonaciones();
                if (!todos.isEmpty()) {
                    camion = todos.get(0);
                }
            }
        }
        if (camion != null) {
            paradas = logisticaApiService.obtenerParadasDelCamion(camion);
            boolean rutaEnLogisticaFinalizada = logisticaApiService.tieneRutaFinalizada(camion);
            boolean todasParadasCompletas = !paradas.isEmpty() && paradas.stream().allMatch(p ->
                    p.getEntrega() != null && (
                            "EXITOSA".equalsIgnoreCase(p.getEntrega().getEstadoActual()) ||
                            "NO_RECIBIDA".equalsIgnoreCase(p.getEntrega().getEstadoActual()) ||
                            "CANCELADA".equalsIgnoreCase(p.getEntrega().getEstadoActual()) ||
                            "EN_DEPOSITO".equalsIgnoreCase(p.getEntrega().getEstadoActual())
                    )
            );
            rutaFinalizada = rutaEnLogisticaFinalizada || todasParadasCompletas;
        }

        model.addAttribute("titulo", "Panel del Chofer - Mi Ruta");
        model.addAttribute("camion", camion);
        model.addAttribute("paradas", paradas);
        model.addAttribute("rutaFinalizada", rutaFinalizada);
        model.addAttribute("idUsuario", idUsuario);
        return "personaChofer/miRuta";
    }

    @PostMapping("/iniciarViaje/{idCamion}")
    @PreAuthorize("hasAuthority('INICIAR_VIAJE')")
    public String iniciarViaje(@PathVariable String idCamion, RedirectAttributes redirectAttributes) {
        try {
            CamionResponse camion = logisticaApiService.buscarCamionPorId(idCamion);
            List<ParadaResponse> paradas = camion != null ? logisticaApiService.obtenerParadasDelCamion(camion) : List.of();
            if (paradas.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeError", "Todavía no hay una ruta con paradas asignadas a este camión. Asigná y pesá las donaciones primero para que el planificador genere la ruta.");
                return "redirect:/personaChofer/miRuta";
            }
            boolean rutaYaCompleta = logisticaApiService.tieneRutaFinalizada(camion) ||
                    (!paradas.isEmpty() && paradas.stream().allMatch(p ->
                            p.getEntrega() != null && (
                                    "EXITOSA".equalsIgnoreCase(p.getEntrega().getEstadoActual()) ||
                                    "NO_RECIBIDA".equalsIgnoreCase(p.getEntrega().getEstadoActual()) ||
                                    "CANCELADA".equalsIgnoreCase(p.getEntrega().getEstadoActual()) ||
                                    "EN_DEPOSITO".equalsIgnoreCase(p.getEntrega().getEstadoActual())
                            )
                    ));

            if (rutaYaCompleta) {
                redirectAttributes.addFlashAttribute("mensajeError", "El recorrido ya fue completado en su totalidad y no puede reiniciarse.");
                return "redirect:/personaChofer/miRuta";
            }

            log.info("Chofer iniciando viaje para camión ID: {}", idCamion);
            logisticaApiService.iniciarViaje(idCamion);

            List<double[]> waypoints = new ArrayList<>();
            // Base de salida: Depósito Central DonaTrack (Medrano 951)
            waypoints.add(new double[]{-34.598500, -58.420100});
            for (ParadaResponse p : paradas) {
                if (p.getEntrega() != null && p.getEntrega().getLatitud() != null && p.getEntrega().getLongitud() != null) {
                    waypoints.add(new double[]{p.getEntrega().getLatitud(), p.getEntrega().getLongitud()});
                }
            }
            if (waypoints.size() < 2) {
                waypoints.add(new double[]{-34.617500, -58.367500});
            }

            RouteResponseDto ruta = osrmService.calcularRutaPuntos(waypoints);
            simulationService.iniciarSimulacion(idCamion, ruta, paradas);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Viaje iniciado con éxito! El camión ha salido del depósito y las donaciones asociadas pasaron a estado 'En traslado'.");
        } catch (Exception e) {
            log.error("Error al iniciar viaje del camión {}: {}", idCamion, e.getMessage());
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo iniciar el viaje: " + e.getMessage());
        }
        return "redirect:/personaChofer/miRuta";
    }

    @PostMapping("/finalizarViaje/{idCamion}")
    @PreAuthorize("hasAuthority('MODIFICAR_ESTADO_CAMION')")
    public String finalizarViaje(@PathVariable String idCamion, RedirectAttributes redirectAttributes) {
        try {
            log.info("Chofer finalizando viaje para camión ID: {}", idCamion);
            logisticaApiService.finalizarViaje(idCamion);
            simulationService.detenerSimulacion(idCamion);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Viaje finalizado con éxito! El camión ha regresado al depósito y las paradas pendientes fueron cerradas por falta de tiempo.");
        } catch (Exception e) {
            log.error("Error al finalizar viaje del camión {}: {}", idCamion, e.getMessage());
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo finalizar el viaje: " + e.getMessage());
        }
        return "redirect:/personaChofer/miRuta";
    }

    @PostMapping("/cancelarEntrega/{idEntrega}")
    @PreAuthorize("hasAuthority('CANCELAR_MI_ENTREGA')")
    public String cancelarEntrega(@PathVariable Long idEntrega,
                                  @RequestParam("motivo") String motivo,
                                  RedirectAttributes redirectAttributes) {
        try {
            log.info("Chofer cancelando entrega ID: {} con motivo: {}", idEntrega, motivo);
            logisticaApiService.cancelarEntrega(idEntrega, motivo);
            simulationService.cancelarEntregaParada(idEntrega);
            redirectAttributes.addFlashAttribute("mensajeExito", "La entrega #" + idEntrega + " fue cancelada correctamente.");
        } catch (Exception e) {
            log.error("Error al cancelar entrega {}: {}", idEntrega, e.getMessage());
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo cancelar la entrega: " + e.getMessage());
        }
        return "redirect:/personaChofer/miRuta";
    }
}

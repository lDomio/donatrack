package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonanteDto;
import ar.edu.utn.frba.dds.front_end_app.dto.*;
import ar.edu.utn.frba.dds.front_end_app.exceptions.BackendNoDisponibleException;
import ar.edu.utn.frba.dds.front_end_app.services.AccesoServiceImpl;
import ar.edu.utn.frba.dds.front_end_app.services.DonacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AccesoController {

  private static final Logger log = LoggerFactory.getLogger(AccesoController.class);
  private final AccesoServiceImpl accesoServiceImpl;
  private final DonacionService donacionService;

  public AccesoController(AccesoServiceImpl accesoServiceImpl, DonacionService donacionService) {
    this.accesoServiceImpl = accesoServiceImpl;
    this.donacionService = donacionService;
  }

  @GetMapping({"/", "/acceso/main"})
  public String home(Model model) {
    List<DonanteDto> donantes;
    boolean errorCarga = false;
    try {
      donantes = accesoServiceImpl.getAll();
    } catch (BackendNoDisponibleException e) {
      donantes = List.of();
      errorCarga = true;
    }
    List<CasoExitoDto> casosExito = donacionService.obtenerCasosExito();
    String casosExitoJson = "[]";
    try {
      casosExitoJson = new tools.jackson.databind.ObjectMapper().writeValueAsString(casosExito);
    } catch (Exception ignored) {
    }
    model.addAttribute("titulo", "Página principal");
    model.addAttribute("vistaActual", "inicio");
    model.addAttribute("cantidadDonantes", donantes != null ? donantes.size() : 0);
    model.addAttribute("donantes", donantes);
    model.addAttribute("casosExito", casosExito);
    model.addAttribute("casosExitoJson", casosExitoJson);
    model.addAttribute("errorCarga", errorCarga);
    return "acceso/main";
  }

  @GetMapping({"/quienesSomos", "/acceso/quienesSomos"})
  public String quienesSomos(Model model) {
    model.addAttribute("titulo", "Quiénes somos");
    model.addAttribute("vistaActual", "quienesSomos");
    return "acceso/quienesSomos";
  }

  @GetMapping({"/empresas", "/acceso/empresas"})
  public String empresas(Model model) {
    model.addAttribute("titulo", "Empresas");
    model.addAttribute("vistaActual", "empresas");
    return "acceso/empresas";
  }

  @GetMapping({"/programas", "/acceso/programas"})
  public String programas(Model model) {
    model.addAttribute("titulo", "Programas");
    model.addAttribute("vistaActual", "programas");
    return "acceso/programas";
  }

  @GetMapping({"/proyectos", "/acceso/proyectos"})
  public String proyectos(Model model) {
    model.addAttribute("titulo", "Proyectos");
    model.addAttribute("vistaActual", "proyectos");
    return "acceso/proyectos";
  }

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("titulo", "Login");
    return "acceso/login";
  }

  @GetMapping("/403")
  public String accessDenied(Model model) {
    model.addAttribute("titulo", "Acceso Denegado");
    return "error/403";
  }
}

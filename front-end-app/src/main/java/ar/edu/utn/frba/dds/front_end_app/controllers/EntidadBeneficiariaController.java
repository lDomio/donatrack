package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaResponseSinSubcategoria;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.NecesidadDTO;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.NecesidadRequest;
import ar.edu.utn.frba.dds.front_end_app.exceptions.ValidationException;
import ar.edu.utn.frba.dds.front_end_app.services.DonacionService;
import ar.edu.utn.frba.dds.front_end_app.services.EntidadBeneficiariaServiceImpl;
import ar.edu.utn.frba.dds.front_end_app.services.rutas.SimulationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.logistica.LogisticaApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/entidadBeneficiaria")
@Controller
public class EntidadBeneficiariaController {
  private final EntidadBeneficiariaServiceImpl entidadBeneficiariaService;
  private final DonacionService donacionService;
  private final SimulationService simulationService;
  private final LogisticaApiService logisticaApiService;

  public EntidadBeneficiariaController(
      EntidadBeneficiariaServiceImpl entidadBeneficiariaService,
      DonacionService donacionService,
      SimulationService simulationService,
      LogisticaApiService logisticaApiService) {
    this.entidadBeneficiariaService = entidadBeneficiariaService;
    this.donacionService = donacionService;
    this.simulationService = simulationService;
    this.logisticaApiService = logisticaApiService;
  }

  @GetMapping("")
  public String home(Model model) {
    return "redirect:/entidadBeneficiaria/misNecesidades";
  }


  private Long obtenerIdEntidad(HttpSession session) {
    Object idObj = session.getAttribute("idEntidad");
    if (idObj != null) {
      try {
        return Long.parseLong(idObj.toString());
      } catch (NumberFormatException ignored) {}
    }
    Object idUsuarioObj = session.getAttribute("idUsuarioAuth");
    if (idUsuarioObj != null) {
      try {
        Long usuarioId = Long.parseLong(idUsuarioObj.toString());
        Long entId = entidadBeneficiariaService.getEntidadIdByUsuarioId(usuarioId);
        if (entId != null) {
          session.setAttribute("idEntidad", entId);
          return entId;
        }
      } catch (Exception e) {
        System.err.println("Error al obtener idEntidad por usuarioId " + idUsuarioObj + ": " + e.getMessage());
      }
      try {
        Long usuarioId = Long.parseLong(idUsuarioObj.toString());
        if (usuarioId == 4L) {
          session.setAttribute("idEntidad", 2L);
          return 2L;
        } else if (usuarioId == 3L) {
          session.setAttribute("idEntidad", 1L);
          return 1L;
        }
      } catch (Exception ignored) {}
    }
    return 1L;
  }

  @GetMapping("/donacionesAsignadas")
  public String donacionesAsignadas(Model model, HttpSession session){
    long id = obtenerIdEntidad(session);
    List<DonacionResponseEntidad> donaciones = entidadBeneficiariaService.getDonacionesByIdEntidad(id);
    model.addAttribute("titulo", "Donaciones asignadas");
    model.addAttribute("vistaActual", "donacionesAsignadas");
    model.addAttribute("donaciones",donaciones);
    return "entidadBeneficiaria/donacionesAsignadas";
  }
  @GetMapping("/misNecesidades")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') or hasAnyAuthority('VER_MIS_NECESIDADES')")
  public String misNecesidades(Model model, Authentication authentication, HttpSession session) {
    long id = obtenerIdEntidad(session);
    List<NecesidadDTO>  necesidades = entidadBeneficiariaService.getNecesidadesByIdEntidad(id);
    model.addAttribute("titulo", "Mis necesidades");
    model.addAttribute("vistaActual", "misNecesidades");
    model.addAttribute("necesidades",necesidades);
    return "entidadBeneficiaria/misNecesidades";
  }

  @GetMapping("/nuevaNecesidad")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') or hasAnyAuthority('CREAR_NECESIDAD_MIA')")
  public String nuevaNecesidad(Model model, HttpSession session) {
    long id = obtenerIdEntidad(session);
    List<CategoriaResponseSinSubcategoria> categorias = entidadBeneficiariaService.getCategorias();
    model.addAttribute("titulo", "Nueva necesidad");
    model.addAttribute("vistaActual", "nuevaNecesidad");
    model.addAttribute("necesidad", new NecesidadRequest());
    model.addAttribute("categorias", categorias);
    return "entidadBeneficiaria/nuevaNecesidad";
  }
  @PostMapping("/crearNecesidad")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') or hasAnyAuthority('CREAR_NECESIDAD_MIA')")
  public String crearNecesidad(HttpSession session, @ModelAttribute("necesidad") NecesidadRequest necesidad,
                               BindingResult bindingResult, Model model, @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                               @RequestParam(value = "dias", required = false) Integer diasSeleccionados,
                               @RequestParam(value = "meses", required = false) Long mesesSeleccionados,
                               @RequestParam(value = "anios", required = false) Long aniosSeleccionados,
                               @RequestParam(value = "cantidadPreConversion", required = false) Long cantidadSeleccionada,
                               @RequestParam(value = "unidad", required = false) String unidadSeleccionada)
  {
    try{
      long entidadId = obtenerIdEntidad(session);
      System.out.println(necesidad);
      entidadBeneficiariaService.crearNecesidad(entidadId, necesidad);
    }
    catch (ValidationException e){
      convertirValidationExceptionABindingResult(e,bindingResult);
      List<CategoriaResponseSinSubcategoria> categorias = entidadBeneficiariaService.getCategorias();
      List<SubcategoriaResponse> subcategorias = entidadBeneficiariaService.getSubcategorias(categoriaId);
      model.addAttribute("categorias", categorias);
      model.addAttribute("categoriaSeleccionadaId", categoriaId);
      model.addAttribute("diasSeleccionados", diasSeleccionados);
      model.addAttribute("mesesSeleccionados", mesesSeleccionados);
      model.addAttribute("aniosSeleccionados", aniosSeleccionados);
      model.addAttribute("cantidadSeleccionada", cantidadSeleccionada);
      model.addAttribute("unidadSeleccionada", unidadSeleccionada);
      model.addAttribute("subcategorias", subcategorias);
      return "entidadBeneficiaria/nuevaNecesidad";
    }
    return "redirect:/entidadBeneficiaria/misNecesidades";
  }
  @GetMapping("/subcategorias")
  @ResponseBody
  public List<SubcategoriaResponse> obtenerSubcategorias(Model model, @RequestParam Long categoriaId) {
    return entidadBeneficiariaService.getSubcategorias(categoriaId);
  }
  @GetMapping("/donacionesEnMovimiento")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') or hasAnyAuthority('VER_DONACIONES_ASIGNADAS')")
  public String donacionesEnMovimiento(Model model, HttpSession session) {
    long id = obtenerIdEntidad(session);
    List<DonacionResponseEntidad> donacionesEnTraslado = entidadBeneficiariaService.getDonacionesEnTraslado(id);
    var entidad = entidadBeneficiariaService.getEntidadById(id);
    String destNombre = (entidad != null && entidad.razonSocial() != null)
        ? entidad.razonSocial() : "Comedor Rayito de Sol";

    double destLat = (id == 2L) ? -34.601500 : -34.617500;
    double destLng = (id == 2L) ? -58.452000 : -58.367500;

    model.addAttribute("titulo", "Donaciones en movimiento");
    model.addAttribute("vistaActual", "donacionesEnMovimiento");
    model.addAttribute("donaciones", donacionesEnTraslado);
    model.addAttribute("viajeActivo", !donacionesEnTraslado.isEmpty());
    model.addAttribute("origenLat", -34.598500);
    model.addAttribute("origenLng", -58.420100);
    model.addAttribute("destinoLat", destLat);
    model.addAttribute("destinoLng", destLng);
    model.addAttribute("destinoNombre", destNombre);
    return "entidadBeneficiaria/donacionesEnMovimiento";
  }
  @PostMapping("/confirmarRecepcion/{idDonacion}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') or hasAnyAuthority('CONFIRMAR_RECEPCION_DE_DONACION_ASIGNADA')")
  public String confirmarRecepcion(
      Model model,
      HttpSession session,
      @PathVariable Long idDonacion,
      @RequestParam(value = "fotoEntrega", required = false) org.springframework.web.multipart.MultipartFile fotoEntrega,
      HttpServletRequest request,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes){
    try {
      long idEntidad = obtenerIdEntidad(session);
      String fotoBase64 = null;
      if (fotoEntrega != null && !fotoEntrega.isEmpty()) {
        try {
          String contentType = fotoEntrega.getContentType() != null ? fotoEntrega.getContentType() : "image/jpeg";
          String base64 = java.util.Base64.getEncoder().encodeToString(fotoEntrega.getBytes());
          fotoBase64 = "data:" + contentType + ";base64," + base64;
        } catch (Exception e) {
          System.err.println("Error al codificar imagen: " + e.getMessage());
        }
      }
      entidadBeneficiariaService.confirmarDonacionAsignadaAEntidad(idDonacion, idEntidad, fotoBase64);
      simulationService.confirmarEntregaParada(idDonacion);
      logisticaApiService.confirmarEntregaExitosaPorDonacion(idDonacion);
      redirectAttributes.addFlashAttribute("mensajeExito", "¡Recepción de la donación confirmada con éxito!");
    } catch (Exception e) {
      System.err.println("Error al confirmar recepción: " + e.getMessage());
      redirectAttributes.addFlashAttribute("mensajeError", "No se pudo confirmar la recepción: " + e.getMessage());
    }
    String referer = request.getHeader("Referer");
    return "redirect:" + (referer != null ? referer : "/entidadBeneficiaria/donacionesAsignadas");
  }
  @GetMapping("/detalleNecesidad/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') or hasAnyAuthority('VER_MIS_NECESIDADES')")
  public String detalleNecesidad(Model model, HttpSession session, @PathVariable Long id) {
    Long idEntidad = obtenerIdEntidad(session);
    NecesidadDTO necesidad = entidadBeneficiariaService.getNecesidadById(idEntidad ,id);
    List<DonacionResponseEntidad> donaciones = donacionService.getDonacionesByNecesidad(id);
    System.out.println("Llegaron las donaciones: "+ donaciones);

    model.addAttribute("titulo", "Detalle necesidad");
    model.addAttribute("vistaActual", "detalleNecesidad");
    model.addAttribute("necesidad", necesidad);
    model.addAttribute("periodo", formatearPeriodo(necesidad.periodoRecurrencia()));
    model.addAttribute("donaciones",donaciones);
    return "entidadBeneficiaria/detalleNecesidad";
  }
  public static String formatearPeriodo(Period periodo) {
    if (periodo == null) {
      return "Período no especificado";
    }
    if (periodo.isZero()) {
      return "0 días";
    }

    List<String> partes = new ArrayList<>();

    int anios = periodo.getYears();
    int meses = periodo.getMonths();
    int dias = periodo.getDays();

    if (anios > 0) {
      partes.add(anios + (anios == 1 ? " año" : " años"));
    }
    if (meses > 0) {
      partes.add(meses + (meses == 1 ? " mes" : " meses"));
    }
    if (dias > 0) {
      partes.add(dias + (dias == 1 ? " día" : " días"));
    }

    // Unimos las partes con formato amigable
    if (partes.size() == 1) {
      return partes.get(0);
    } else if (partes.size() == 2) {
      return partes.get(0) + " y " + partes.get(1);
    } else {
      return partes.get(0) + ", " + partes.get(1) + " y " + partes.get(2);
    }
  }
  private void convertirValidationExceptionABindingResult(ValidationException e, BindingResult bindingResult) {
    if(e.hasFieldErrors()) {
      e.getFieldErrors().forEach((field, error) -> bindingResult.rejectValue(field, "error." + field, error));
    }
  }
}

package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaResponseSinSubcategoria;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.UsuarioResponseDTO;
import ar.edu.utn.frba.dds.front_end_app.exceptions.UnauthorizedException;
import ar.edu.utn.frba.dds.front_end_app.exceptions.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonanteDto;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.RegistroEntidadBeneficiariaForm;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.CamionRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.logistica.CamionResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.ranking.DonanteRankingItemDto;
import ar.edu.utn.frba.dds.front_end_app.dto.ranking.RankingResponseDto;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.auth.AuthApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonanteApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.EntidadBeneficiariaApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.IncentivosApiService;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.RegistroDonanteAdminForm;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.SugerenciaMatchResponse;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonacionApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.logistica.LogisticaApiService;
import ar.edu.utn.frba.dds.front_end_app.services.internal.AlmacenamientoFotosService;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/personaAdministradora")
@Controller
public class PersonaAdministradoraController {
  private static final Logger log = LoggerFactory.getLogger(PersonaAdministradoraController.class);

  private final DonanteApiService donanteApiService;
  private final LogisticaApiService logisticaApiService;
  private final IncentivosApiService incentivosApiService;
  private final EntidadBeneficiariaApiService entidadBeneficiariaApiService;
  private final AuthApiService authApiService;
  private final DonacionApiService donacionApiService;
  private final AlmacenamientoFotosService almacenamientoFotosService;

  public PersonaAdministradoraController(
      DonanteApiService donanteApiService,
      LogisticaApiService logisticaApiService,
      IncentivosApiService incentivosApiService,
      EntidadBeneficiariaApiService entidadBeneficiariaApiService,
      AuthApiService authApiService,
      DonacionApiService donacionApiService,
      AlmacenamientoFotosService almacenamientoFotosService) {
    this.donanteApiService = donanteApiService;
    this.logisticaApiService = logisticaApiService;
    this.incentivosApiService = incentivosApiService;
    this.entidadBeneficiariaApiService = entidadBeneficiariaApiService;
    this.authApiService = authApiService;
    this.donacionApiService = donacionApiService;
    this.almacenamientoFotosService = almacenamientoFotosService;
  }

  @GetMapping("/")
  public String home(Model model) {
    return "redirect:/personaAdministradora/gestionDeDonantes";
  }

  @GetMapping("/gestionDeDonantes")
  @PreAuthorize("hasAuthority('VER_DONANTES')")
  public String gestionDeDonantes(
      @RequestParam(required = false) String buscar,
      @RequestParam(required = false, defaultValue = "todos") String tipo,
      @RequestParam(required = false, defaultValue = "todos") String estado,
      @RequestParam(required = false, defaultValue = "1") int pagina,
      Model model,
      Authentication auth) {
    if(Objects.equals(estado, "baja")){
      if(auth.getAuthorities().stream()
          .noneMatch(a -> Objects.equals(a.getAuthority(), "VER_DONANTES_INACTIVOS"))){
        throw new UnauthorizedException("No tiene permiso para ver donantes inactivos");
      }
    }
    List<DonanteDto> todosDonantes = donanteApiService.getAll(buscar, tipo, estado);
    int totalDonantes = todosDonantes.size();
    int tamanoPagina = 20;

    int totalPaginas = (int) Math.ceil((double) totalDonantes / tamanoPagina);
    if (totalPaginas == 0) {
      totalPaginas = 1;
    }

    int paginaActual = Math.max(1, Math.min(pagina, totalPaginas));
    int indiceInicio = (paginaActual - 1) * tamanoPagina;
    int indiceFin = Math.min(indiceInicio + tamanoPagina, totalDonantes);

    List<DonanteDto> donantesPaginados;
    if (totalDonantes > 0 && indiceInicio < totalDonantes) {
      donantesPaginados = todosDonantes.subList(indiceInicio, indiceFin);
    } else {
      donantesPaginados = List.of();
    }

    int registroDesde = totalDonantes > 0 ? (indiceInicio + 1) : 0;
    int registroHasta = indiceFin;

    model.addAttribute("donantes", donantesPaginados);
    model.addAttribute("paramBuscar", buscar);
    model.addAttribute("paramTipo", tipo);
    model.addAttribute("paramEstado", estado);
    model.addAttribute("paginaActual", paginaActual);
    model.addAttribute("totalPaginas", totalPaginas);
    model.addAttribute("totalDonantes", totalDonantes);
    model.addAttribute("registroDesde", registroDesde);
    model.addAttribute("registroHasta", registroHasta);
    model.addAttribute("titulo", "Gestión de donantes");
    model.addAttribute("vistaActual", "gestionDeDonantes");
    return "personaAdministradora/gestionDeDonantes";
  }

  @GetMapping("/registroDeDonaciones")
  @PreAuthorize("hasAuthority('VER_DONANTES') and hasAuthority('REGISTRAR_DONANTE')")
  public String registroDeDonaciones(Model model) {
    List<DonanteDto> donantes = donanteApiService.getAll();
    List<SubcategoriaResponse> subcategorias = donacionApiService.obtenerSubcategorias();
    List<CategoriaResponseSinSubcategoria> categorias = donacionApiService.obtenerCategorias();
    model.addAttribute("donantes", donantes);
    model.addAttribute("subcategorias", subcategorias);
    model.addAttribute("categorias", categorias);
    model.addAttribute("titulo", "Registro de donaciones");
    model.addAttribute("vistaActual", "registroDeDonaciones");
    return "personaAdministradora/registroDeDonaciones";
  }

  @PostMapping("/subcategorias")
  @ResponseBody
  @PreAuthorize("hasAuthority('CREAR_SUBCATEGORIAS')")
  public ResponseEntity<?> crearSubcategoria(@RequestBody SubcategoriaRequest request) {
    try {
      SubcategoriaResponse sub = donacionApiService.crearSubcategoria(request);
      return ResponseEntity.ok(sub);
    } catch (Exception e) {
      log.error("Error al crear subcategoría: {}", e.getMessage());
      return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al crear subcategoría"));
    }
  }

  @PostMapping("/fotos")
  @ResponseBody
  @PreAuthorize("hasAuthority('REGISTRAR_DONANTE')")
  public ResponseEntity<?> subirFotoDeBien(@RequestParam("archivo") MultipartFile archivo) {
    try {
      return ResponseEntity.ok(java.util.Map.of("url", almacenamientoFotosService.guardar(archivo)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
    } catch (Exception e) {
      log.error("Error al guardar la foto del bien: {}", e.getMessage());
      return ResponseEntity.internalServerError().body(java.util.Map.of("error", "No se pudo guardar la imagen."));
    }
  }

  @PostMapping("/registroDeDonaciones")
  @PreAuthorize("hasAuthority('REGISTRAR_DONACION')")
  public String registrarDonacionEnDeposito(
      @RequestParam Long idDonante,
      @RequestParam(required = false) String descripcionDonacion,
      @RequestParam(required = false) String bienesJson,
      @RequestParam(required = false) String nombreItem,
      @RequestParam(required = false) Double cantidadItem,
      @RequestParam(required = false) String estadoItem,
      RedirectAttributes redirectAttributes) {
    try {
      if (bienesJson != null && !bienesJson.isBlank()) {
        donacionApiService.registrarDonacionEnDeposito(idDonante, descripcionDonacion, bienesJson);
      } else {
        donacionApiService.registrarDonacionEnDeposito(idDonante, descripcionDonacion, nombreItem, cantidadItem, estadoItem);
      }
      redirectAttributes.addFlashAttribute("exito", "Donación registrada en depósito con éxito.");
      return "redirect:/personaAdministradora/asignacionDeDonaciones";
    }
    catch(ValidationException e){
      redirectAttributes.addFlashAttribute("error", formatearErrores(e.getFieldErrors()));
      return "redirect:/personaAdministradora/registroDeDonaciones";
    }
    catch (Exception e) {
      log.error("Error al registrar donación en depósito: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo registrar la donación: " + e.getMessage());
      return "redirect:/personaAdministradora/registroDeDonaciones";
    }
  }

  @GetMapping("/asignacionDeDonaciones")
  @PreAuthorize("hasAuthority('VER_TODAS_LAS_DONACIONES') and hasAuthority('VER_MATCHEOS')")
  public String asignacionDeDonaciones(
      @RequestParam(required = false) String estado,
      @RequestParam(required = false) String buscar,
      Model model) {
    List<DonacionResponseEntidad> donaciones = donacionApiService.obtenerTodasLasDonaciones();
    List<SugerenciaMatchResponse> matcheos = donacionApiService.obtenerSugerenciasDeMatcheo();

    if (estado != null && !estado.isBlank() && !estado.equalsIgnoreCase("todos")) {
      donaciones = donaciones.stream()
          .filter(d -> d.estado() != null && d.estado().name().equalsIgnoreCase(estado))
          .toList();
    }
    if (buscar != null && !buscar.isBlank()) {
      String b = buscar.trim().toLowerCase();
      donaciones = donaciones.stream()
          .filter(d -> (d.id() != null && d.id().toString().contains(b)) ||
                       (d.descripcionGeneral() != null && d.descripcionGeneral().toLowerCase().contains(b)))
          .toList();
    }

    model.addAttribute("donaciones", donaciones);
    model.addAttribute("matcheos", matcheos);
    model.addAttribute("paramEstado", estado);
    model.addAttribute("paramBuscar", buscar);
    model.addAttribute("totalDonaciones", donaciones.size());
    model.addAttribute("titulo", "Asignación de donaciones");
    model.addAttribute("vistaActual", "asignacionDeDonaciones");
    return "personaAdministradora/asignacionDeDonaciones";
  }

  @PostMapping("/donaciones/{id}/vencer")
  @PreAuthorize("hasAuthority('CAMBIAR_ESTADO_DONACION')")
  public String vencerDonacion(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    try {
      donacionApiService.marcarComoVencida(id);
      redirectAttributes.addFlashAttribute("exito", "Donación #" + id + " marcada como vencida.");
    } catch (Exception e) {
      log.error("Error al marcar donación como vencida: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo marcar la donación como vencida: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/asignacionDeDonaciones";
  }

  @PostMapping("/donaciones/{id}/reingresar")
  @PreAuthorize("hasAuthority('CAMBIAR_ESTADO_DONACION')")
  public String reingresarDonacionAlDeposito(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    try {
      donacionApiService.reingresarAlDeposito(id);
      redirectAttributes.addFlashAttribute("exito", "Donación #" + id + " reingresada al depósito con éxito y disponible para ser asignada nuevamente.");
    } catch (Exception e) {
      log.error("Error al reingresar donación al depósito: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo reingresar la donación al depósito: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/asignacionDeDonaciones";
  }

  @PostMapping("/donaciones/{id}/pesaje")
  @PreAuthorize("hasAnyAuthority('CAMBIAR_ESTADO_DONACION', 'VER_TODAS_LAS_DONACIONES')")
  public String registrarPesaje(
      @PathVariable("id") Long id,
      @RequestParam("peso") Double peso,
      @RequestParam("volumen") Double volumen,
      RedirectAttributes redirectAttributes) {
    try {
      // Regla de negocio: La donación debe estar asignada previamente a una entidad
      List<DonacionResponseEntidad> todas = donacionApiService.obtenerTodasLasDonaciones();
      DonacionResponseEntidad donacion = todas.stream()
          .filter(d -> Objects.equals(d.id(), id))
          .findFirst().orElse(null);
      if (donacion != null && donacion.estado() != null && "DEPOSITO".equalsIgnoreCase(donacion.estado().name())) {
        redirectAttributes.addFlashAttribute("error", "No se puede registrar el pesaje de la Donación #" + id + " porque aún está en depósito. Primero debe asignarse a una entidad beneficiaria.");
        return "redirect:/personaAdministradora/asignacionDeDonaciones";
      }

      logisticaApiService.registrarPesaje(id, peso, volumen);
      redirectAttributes.addFlashAttribute("exito", "Pesaje registrado con éxito para la Donación #" + id + " (" + peso + " kg, " + volumen + " m³). Donación lista para planificar ruta.");
    } catch (Exception e) {
      log.error("Error al registrar pesaje para la donación #{}: {}", id, e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo registrar el pesaje: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/asignacionDeDonaciones";
  }

  @PostMapping("/matcheos/ejecutar")
  @PreAuthorize("hasAnyAuthority('VER_MATCHEOS', 'SELECCIONAR_MATCHEO')")
  public String ejecutarMatcheoADemanda(RedirectAttributes redirectAttributes) {
    try {
      donacionApiService.ejecutarMatcheo();
      redirectAttributes.addFlashAttribute("exito", "Matchmaking ejecutado a demanda con éxito. Se actualizaron las sugerencias de Compatibilidad Semántica y Prioridad Sub-atendidos.");
    } catch (Exception e) {
      log.error("Error al ejecutar matchmaking a demanda: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo ejecutar el matchmaking: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/asignacionDeDonaciones";
  }

  @PostMapping({"/matcheos/{idMatcheo}/asignar", "/matcheos/{idMatcheo}/asignar/{idNecesidadPath}"})
  @PreAuthorize("hasAuthority('SELECCIONAR_MATCHEO')")
  public String asignarMatcheo(
      @PathVariable("idMatcheo") Long idMatcheo,
      @PathVariable(value = "idNecesidadPath", required = false) String idNecesidadPath,
      @RequestParam(value = "idNecesidad", required = false) Long idNecesidadParam,
      RedirectAttributes redirectAttributes) {
    try {
      Long idNecesidad = idNecesidadParam;
      if (idNecesidad == null && idNecesidadPath != null) {
        idNecesidad = Long.parseLong(idNecesidadPath);
      }
      donacionApiService.seleccionarMatcheo(idMatcheo, idNecesidad);
      redirectAttributes.addFlashAttribute("exito", "Donación asignada correctamente a la entidad beneficiaria.");
    } catch (Exception e) {
      log.error("Error al asignar matcheo: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo asignar la donación: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/asignacionDeDonaciones";
  }

  @GetMapping("/camiones")
  @PreAuthorize("hasAuthority('GESTIONAR_CAMIONES')")
  public String camiones(Model model) {
    List<CamionResponse> camiones = logisticaApiService.obtenerTodosLosCamionesConDonaciones();
    model.addAttribute("camiones", camiones);
    model.addAttribute("totalCamiones", camiones.size());
    model.addAttribute("titulo", "Camiones");
    model.addAttribute("vistaActual", "camiones");
    return "personaAdministradora/camiones";
  }

  @PostMapping("/camiones")
  @PreAuthorize("hasAuthority('GESTIONAR_CAMIONES')")
  public String agregarCamion(CamionRequest request, RedirectAttributes redirectAttributes) {
      try {
          logisticaApiService.agregarCamion(request);
          redirectAttributes.addFlashAttribute("mensajeExito", "Camión registrado con éxito.");
      } catch (Exception e) {
          redirectAttributes.addFlashAttribute("mensajeError", "Error al agregar camión: " + e.getMessage());
      }
      return "redirect:/personaAdministradora/camiones";
  }

  @PostMapping("/camiones/{id}/eliminar")
  @PreAuthorize("hasAuthority('GESTIONAR_CAMIONES')")
  public String eliminarCamion(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
      try {
          logisticaApiService.eliminarCamion(id);
          redirectAttributes.addFlashAttribute("mensajeExito", "Camión eliminado correctamente.");
      } catch (Exception e) {
          redirectAttributes.addFlashAttribute("mensajeError", "No se pudo eliminar el camión: " + e.getMessage());
      }
      return "redirect:/personaAdministradora/camiones";
  }



  @GetMapping("/ranking")
  @PreAuthorize("hasAuthority('VER_RANKING')")
  public String ranking(
      @RequestParam(required = false) String periodo,
      @RequestParam(required = false) Integer mes,
      @RequestParam(required = false) Integer anio,
      Model model) {
    LocalDate hoy = LocalDate.now();
    int mesConsulta = hoy.getMonthValue();
    int anioConsulta = hoy.getYear();

    if (periodo != null && periodo.matches("^\\d{4}-\\d{1,2}$")) {
      String[] partes = periodo.split("-");
      anioConsulta = Integer.parseInt(partes[0]);
      mesConsulta = Integer.parseInt(partes[1]);
    } else {
      if (mes != null && mes >= 1 && mes <= 12) {
        mesConsulta = mes;
      }
      if (anio != null && anio >= 2000) {
        anioConsulta = anio;
      }
    }

    RankingResponseDto rankingResponse = incentivosApiService.obtenerRanking(mesConsulta, anioConsulta);
    List<DonanteRankingItemDto> rankingOriginal = rankingResponse != null ? rankingResponse.getDonantesOEmpty() : List.of();
    List<DonanteRankingItemDto> rankingAux = new ArrayList<>(rankingOriginal);
    rankingAux.sort(java.util.Comparator.comparingInt((DonanteRankingItemDto d) -> d.cantidadDonaciones() != null ? d.cantidadDonaciones() : 0).reversed());
    List<DonanteRankingItemDto> ranking = new ArrayList<>();
    int puestoCalc = 1;
    for (DonanteRankingItemDto item : rankingAux) {
        ranking.add(new DonanteRankingItemDto(
            puestoCalc++,
            item.idDonante(),
            item.idDonanteDonacionesService(),
            item.nombreUsuario(),
            item.cantidadDonaciones(),
            item.misionesCompletadas()
        ));
    }
    int totalDonantes = rankingResponse != null ? rankingResponse.getTotal() : ranking.size();

    String periodoSeleccionado = String.format("%04d-%02d", anioConsulta, mesConsulta);
    String nombreMes = Month.of(mesConsulta).getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    nombreMes = Character.toUpperCase(nombreMes.charAt(0)) + nombreMes.substring(1);
    String periodoTexto = nombreMes + " " + anioConsulta;

    // Generar opciones de período para los últimos 8 meses
    List<PeriodoOpcion> opcionesPeriodo = new ArrayList<>();
    LocalDate cursor = LocalDate.of(hoy.getYear(), hoy.getMonthValue(), 1);
    for (int i = 0; i < 8; i++) {
        String val = String.format("%04d-%02d", cursor.getYear(), cursor.getMonthValue());
        String mesNombre = cursor.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        mesNombre = Character.toUpperCase(mesNombre.charAt(0)) + mesNombre.substring(1);
        String label = mesNombre + " " + cursor.getYear();
        opcionesPeriodo.add(new PeriodoOpcion(val, label));
        cursor = cursor.minusMonths(1);
    }

    model.addAttribute("ranking", ranking);
    model.addAttribute("totalDonantes", totalDonantes);
    model.addAttribute("periodoSeleccionado", periodoSeleccionado);
    model.addAttribute("periodoTexto", periodoTexto);
    model.addAttribute("opcionesPeriodo", opcionesPeriodo);
    model.addAttribute("mesSeleccionado", mesConsulta);
    model.addAttribute("anioSeleccionado", anioConsulta);
    model.addAttribute("titulo", "Ranking");
    model.addAttribute("vistaActual", "ranking");
    return "personaAdministradora/ranking";
  }

  @PostMapping("/ranking/generar")
  @PreAuthorize("hasAuthority('VER_RANKING')")
  public String generarRanking(
      @RequestParam String periodo,
      RedirectAttributes redirectAttributes) {
    Integer mes = null;
    Integer anio = null;
    if (periodo != null && periodo.matches("^\\d{4}-\\d{1,2}$")) {
      String[] partes = periodo.split("-");
      anio = Integer.parseInt(partes[0]);
      mes = Integer.parseInt(partes[1]);
    }

    if (mes == null || mes < 1 || mes > 12 || anio == null || anio < 2000) {
      redirectAttributes.addFlashAttribute("error", "El período seleccionado no es válido.");
      return "redirect:/personaAdministradora/ranking";
    }

    RankingResponseDto response = incentivosApiService.generarRanking(mes, anio);
    if (response == null) {
      redirectAttributes.addFlashAttribute("error", "No se pudo generar el ranking. Intentá de nuevo.");
    } else {
      redirectAttributes.addFlashAttribute("exito", "Ranking generado correctamente para " + periodo + ".");
    }
    return "redirect:/personaAdministradora/ranking?periodo=" + periodo;
  }

  public record PeriodoOpcion(String valor, String etiqueta) {}

  @GetMapping("/registrarDonante")
  @PreAuthorize("hasAuthority('REGISTRAR_DONANTE')")
  public String registrarDonante(Model model) {
    model.addAttribute("titulo", "Registrar donante");
    model.addAttribute("vistaActual", "registrarDonante");
    return "personaAdministradora/registrarDonante";
  }

  @PostMapping("/registrarDonante")
  @PreAuthorize("hasAuthority('REGISTRAR_DONANTE')")
  public String procesarRegistroDonante(
      @ModelAttribute RegistroDonanteAdminForm form,
      RedirectAttributes redirectAttributes) {
    try {
      UsuarioResponseDTO usuarioCreado = null;
      if (form.username() != null && !form.username().isBlank()) {
        usuarioCreado = authApiService.registrar(
            form.username(), form.password(), form.confirmPassword(),
            form.nombreRazonSocial(), "DONANTE");
      }
      donanteApiService.registrarDesdeFormulario(form, usuarioCreado != null ? usuarioCreado.id() : null);
      if (usuarioCreado != null) {
        redirectAttributes.addFlashAttribute("exito",
            "El donante \"" + form.nombreRazonSocial() + "\" se registró correctamente con el usuario " + form.username() + ".");
      } else {
        redirectAttributes.addFlashAttribute("exito",
            "El donante \"" + form.nombreRazonSocial() + "\" se registró correctamente.");
      }
    } catch (Exception e) {
      log.error("Error al registrar donante: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo registrar el donante: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/registrarDonante";
  }

  @PostMapping("/importarDonantesCsv")
  @PreAuthorize("hasAuthority('REGISTRAR_DONANTE')")
  public String importarDonantesCsv(
      @RequestParam("archivoCsv") MultipartFile archivoCsv,
      RedirectAttributes redirectAttributes) {
    try {
      if (archivoCsv == null || archivoCsv.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Por favor seleccione un archivo CSV válido.");
        return "redirect:/personaAdministradora/registrarDonante";
      }

      List<DonanteDto> importados = donanteApiService.importarDonantesCsv(archivoCsv);
      if (importados != null && !importados.isEmpty()) {
        redirectAttributes.addFlashAttribute("exito",
            "Se importaron exitosamente " + importados.size() + " donantes mediante OpenCSV en el backend y se generaron sus credenciales de acceso.");
      } else {
        redirectAttributes.addFlashAttribute("error", "No se pudo importar ningún donante del archivo.");
      }
    } catch (Exception e) {
      log.error("Error al importar donantes desde CSV: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("error", "Error al procesar el archivo CSV: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/registrarDonante";
  }

  @GetMapping("/registrarRepresentante")
  @PreAuthorize("hasAnyAuthority('CREAR_ENTIDAD_BENEFICIARIA', 'EDITAR_DONANTES', 'REGISTRAR_DONANTE')")
  public String registrarRepresentante(
      @RequestParam(name = "idDonante", required = false) Long idDonante,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (idDonante == null) {
      return "redirect:/personaAdministradora/gestionDeDonantes";
    }
    try {
      DonanteDto donante = donanteApiService.obtenerPorId(idDonante);
      if (donante == null) {
        return "redirect:/personaAdministradora/gestionDeDonantes";
      }
      model.addAttribute("donante", donante);
      model.addAttribute("titulo", "Registrar representante");
      model.addAttribute("vistaActual", "registrarRepresentante");
      return "personaAdministradora/registrarRepresentante";
    } catch (Exception e) {
      log.error("Error al cargar persona jurídica {} para registrar representante: {}", idDonante, e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo encontrar la persona jurídica seleccionada.");
      return "redirect:/personaAdministradora/gestionDeDonantes";
    }
  }

  @PostMapping("/registrarRepresentante")
  @PreAuthorize("hasAnyAuthority('CREAR_ENTIDAD_BENEFICIARIA', 'EDITAR_DONANTES', 'REGISTRAR_DONANTE')")
  public String procesarRegistroRepresentante(
      @RequestParam("idDonante") Long idDonante,
      @RequestParam("nombreRepresentante") String nombreRepresentante,
      @RequestParam("apellidoRepresentante") String apellidoRepresentante,
      @RequestParam("emailRepresentante") String emailRepresentante,
      @RequestParam("telefonoRepresentante") String telefonoRepresentante,
      RedirectAttributes redirectAttributes) {
    try {
      donanteApiService.agregarRepresentante(idDonante, nombreRepresentante, apellidoRepresentante, emailRepresentante, telefonoRepresentante);
      redirectAttributes.addFlashAttribute("exito", "Representante registrado correctamente.");
      return "redirect:/personaAdministradora/gestionDeDonantes#modalRepresentantes-" + idDonante;
    } catch (Exception e) {
      log.error("Error al registrar representante para donante {}: {}", idDonante, e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo registrar el representante: " + e.getMessage());
      return "redirect:/personaAdministradora/registrarRepresentante?idDonante=" + idDonante;
    }
  }

  @PostMapping("/donantes/{idDonante}/representantes/{idRepresentante}/editar")
  @PreAuthorize("hasAuthority('EDITAR_DONANTES')")
  public String editarRepresentante(
      @PathVariable("idDonante") Long idDonante,
      @PathVariable("idRepresentante") Long idRepresentante,
      @RequestParam("nombreRepresentante") String nombreRepresentante,
      @RequestParam("apellidoRepresentante") String apellidoRepresentante,
      @RequestParam("correoRepresentante") String correoRepresentante,
      @RequestParam("telefonoRepresentante") String telefonoRepresentante,
      RedirectAttributes redirectAttributes) {
    try {
      donanteApiService.editarRepresentante(idDonante, idRepresentante, nombreRepresentante, apellidoRepresentante, correoRepresentante, telefonoRepresentante);
      redirectAttributes.addFlashAttribute("exito", "Representante actualizado correctamente.");
    } catch (Exception e) {
      log.error("Error al editar representante {} del donante {}: {}", idRepresentante, idDonante, e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo editar el representante: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/gestionDeDonantes#modalRepresentantes-" + idDonante;
  }

  @PostMapping("/donantes/{idDonante}/representantes/{idRepresentante}/eliminar")
  @PreAuthorize("hasAnyAuthority('EDITAR_DONANTES', 'ELIMINAR_DONANTES')")
  public String eliminarRepresentante(
      @PathVariable("idDonante") Long idDonante,
      @PathVariable("idRepresentante") Long idRepresentante,
      RedirectAttributes redirectAttributes) {
    try {
      donanteApiService.eliminarRepresentante(idDonante, idRepresentante);
      redirectAttributes.addFlashAttribute("exito", "Representante eliminado correctamente.");
    } catch (Exception e) {
      log.error("Error al eliminar representante {} del donante {}: {}", idRepresentante, idDonante, e.getMessage());
      redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el representante: " + e.getMessage());
    }
    return "redirect:/personaAdministradora/gestionDeDonantes#modalRepresentantes-" + idDonante;
  }

  @GetMapping("/registrarEntidadBeneficiaria")
  @PreAuthorize("hasAuthority('CREAR_ENTIDAD_BENEFICIARIA')")
  public String registrarEntidadBeneficiaria(Model model) {
    model.addAttribute("titulo", "Registrar entidad beneficiaria");
    model.addAttribute("vistaActual", "registrarEntidadBeneficiaria");
    return "personaAdministradora/registrarEntidadBeneficiaria";
  }

  @PostMapping("/registrarEntidadBeneficiaria")
  @PreAuthorize("hasAuthority('CREAR_ENTIDAD_BENEFICIARIA')")
  public String procesarRegistroEntidadBeneficiaria(
      @ModelAttribute RegistroEntidadBeneficiariaForm form,
      Model model,
      RedirectAttributes redirectAttributes) {
    try {
      UsuarioResponseDTO usuarioCreado = authApiService.registrar(
          form.username(), form.password(), form.confirmPassword(),
          form.razonSocial(), "ENTIDAD_BENEFICIARIA");
      EntidadBeneficiariaDTO entidad = entidadBeneficiariaApiService.crear(
          form.toRequest(usuarioCreado != null ? usuarioCreado.id() : null));
      redirectAttributes.addFlashAttribute("exito",
          "La entidad \"" + entidad.razonSocial() + "\" se registró correctamente.");
      return "redirect:/personaAdministradora/registrarEntidadBeneficiaria";
    } catch (Exception e) {
      log.error("Error al registrar entidad beneficiaria: {}", e.getMessage());
      model.addAttribute("titulo", "Registrar entidad beneficiaria");
      model.addAttribute("vistaActual", "registrarEntidadBeneficiaria");
      model.addAttribute("error", "No se pudo registrar la entidad. Intentá nuevamente.");
      model.addAttribute("form", form);
      return "personaAdministradora/registrarEntidadBeneficiaria";
    }
  }

  @PostMapping("/donantes/{id}/baja")
  @PreAuthorize("hasAuthority('DAR_DE_BAJA_DONATE')")
  public String bajaDonante(@PathVariable("id") Long id) {
      donanteApiService.darDeBajaDonante(id);
      return "redirect:/personaAdministradora/gestionDeDonantes";
  }

  @PostMapping("/donantes/{id}/alta")
  @PreAuthorize("hasAuthority('DAR_DE_ALTA_DONATE')")
  public String altaDonante(@PathVariable("id") Long id) {
      donanteApiService.darDeAltaDonante(id);
      return "redirect:/personaAdministradora/gestionDeDonantes";
  }

  private String formatearErrores(Map<String, String> errores) {
    String detalles = errores.entrySet().stream()
        .map(entry -> entry.getKey() + entry.getValue())
        .collect(Collectors.joining("<br>"));

    return "Corrija los siguientes errores antes de volver a enviar:<br>" + detalles + "<br>";
  }
}

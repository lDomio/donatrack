package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaNecesidadDto;
import ar.edu.utn.frba.dds.front_end_app.dto.DonacionVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadesAyudadasResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadDetalleDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadDetalleVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.IncentivosVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.PinMapaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.PuntoCercanoDto;
import ar.edu.utn.frba.dds.front_end_app.exceptions.BackendNoDisponibleException;
import ar.edu.utn.frba.dds.front_end_app.exceptions.NotFoundException;
import ar.edu.utn.frba.dds.front_end_app.services.IdentidadService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonacionApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.EntidadBeneficiariaApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.DonacionMapper;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.IncentivosApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.mapper.IncentivosMapper;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonanteDto;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonanteApiService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestMapping("/personaDonante")
@Controller
public class PersonaDonanteController {

  private static final Logger log = LoggerFactory.getLogger(PersonaDonanteController.class);

  private final IdentidadService identidadService;
  private final DonacionApiService donacionApiService;
  private final IncentivosApiService incentivosApiService;
  private final EntidadBeneficiariaApiService entidadBeneficiariaApiService;
  private final DonanteApiService donanteApiService;

  @Autowired
  public PersonaDonanteController(
      IdentidadService identidadService,
      DonacionApiService donacionApiService,
      IncentivosApiService incentivosApiService,
      EntidadBeneficiariaApiService entidadBeneficiariaApiService,
      DonanteApiService donanteApiService) {
    this.identidadService = identidadService;
    this.donacionApiService = donacionApiService;
    this.incentivosApiService = incentivosApiService;
    this.entidadBeneficiariaApiService = entidadBeneficiariaApiService;
    this.donanteApiService = donanteApiService;
  }

  public PersonaDonanteController(
      IdentidadService identidadService,
      DonacionApiService donacionApiService,
      IncentivosApiService incentivosApiService,
      EntidadBeneficiariaApiService entidadBeneficiariaApiService) {
    this(identidadService, donacionApiService, incentivosApiService, entidadBeneficiariaApiService, null);
  }

  private Long resolverDonanteId(HttpSession session) {
    if (session == null) {
      return null;
    }
    Object donanteId = session.getAttribute("idDonante");
    if (donanteId == null) {
      donanteId = session.getAttribute("donanteId");
    }
    if (donanteId != null) {
      try {
        return Long.parseLong(donanteId.toString());
      } catch (NumberFormatException ignored) {
      }
    }
    return identidadService != null ? identidadService.donanteIdActual(session) : null;
  }

  @GetMapping("/")
  public String home(Model model) {
    return "redirect:/personaDonante/misDonaciones";
  }

  @GetMapping("/misDonaciones")
  @PreAuthorize("hasAuthority('VER_MIS_DONACIONES')")
  public String misDonaciones(Model model, HttpSession session) {
    Long donanteId = resolverDonanteId(session);
    if (donanteId == null) {
      return "redirect:/login";
    }

    model.addAttribute("titulo", "Mis donaciones");
    model.addAttribute("vistaActual", "misDonaciones");

    List<DonacionVistaDto> donaciones;
    boolean errorCarga = false;
    try {
      donaciones = donacionApiService.obtenerPorDonante(donanteId);
    } catch (BackendNoDisponibleException e) {
      donaciones = List.of();
      errorCarga = true;
    }

    model.addAttribute("donaciones", donaciones);
    model.addAttribute("errorCarga", errorCarga);
    model.addAttribute("estados", DonacionMapper.ESTADOS_DISPONIBLES);
    model.addAttribute("categorias", valoresDistintos(donaciones, DonacionVistaDto::categoria));
    model.addAttribute("subcategorias", valoresDistintos(donaciones, DonacionVistaDto::subcategoria));
    return "personaDonante/misDonaciones";
  }

  private static List<String> valoresDistintos(List<DonacionVistaDto> donaciones, Function<DonacionVistaDto, String> extractor) {
    return donaciones.stream()
        .map(extractor)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  @GetMapping("/entidadesBeneficiarias")
  @PreAuthorize("hasAuthority('VER_ENTIDADES_Y_NECESIDADES')")
  public String entidadesBeneficiarias(
      Model model,
      HttpSession session,
      @RequestParam(required = false) String busqueda,
      @RequestParam(required = false) String tipo) {
    model.addAttribute("titulo", "Entidades beneficiarias");
    model.addAttribute("vistaActual", "entidadesBeneficiarias");

    model.addAttribute("tiposEntidad", List.of("Todas", "Educación", "Comedores", "Salud", "Refugios", "ONG"));
    String tipoActivo = (tipo == null || tipo.isBlank()) ? "Todas" : tipo;
    model.addAttribute("tipoEntidadActivo", tipoActivo);
    model.addAttribute("busquedaActiva", busqueda != null ? busqueda : "");

    List<EntidadVistaDto> entidades;
    boolean errorCargaEntidades = false;
    try {
      entidades = entidadBeneficiariaApiService.obtenerConFiltros(vacioANull(busqueda), vacioANull(tipo));
    } catch (BackendNoDisponibleException e) {
      entidades = List.of();
      errorCargaEntidades = true;
    }

    model.addAttribute("entidades", entidades);
    model.addAttribute("errorCarga", errorCargaEntidades);

    Long donanteId = resolverDonanteId(session);
    boolean mostrarConteo = donanteId != null;
    Long cantidadEntidadesAyudadas = null;
    boolean errorConteo = false;
    if (mostrarConteo) {
      try {
        EntidadesAyudadasResponse conteo = donacionApiService.contarEntidadesAyudadas(donanteId);
        cantidadEntidadesAyudadas = conteo.cantidad();
      } catch (BackendNoDisponibleException e) {
        errorConteo = true;
      }
    }

    model.addAttribute("mostrarConteo", mostrarConteo && !errorConteo);
    model.addAttribute("cantidadEntidadesAyudadas", cantidadEntidadesAyudadas);
    model.addAttribute("errorConteo", errorConteo);

    return "personaDonante/entidadesBeneficiarias";
  }

  private static String vacioANull(String valor) {
    return (valor == null || valor.isBlank()) ? null : valor;
  }

  @GetMapping("/incentivos")
  @PreAuthorize("hasAuthority('VER_MIS_INCENTIVOS')")
  public String incentivos(Model model, HttpSession session) {
    Long donanteId = resolverDonanteId(session);
    if (donanteId == null) {
      return "redirect:/login";
    }

    model.addAttribute("titulo", "Incentivos");
    model.addAttribute("vistaActual", "incentivos");

    IncentivosVistaDto incentivos;
    boolean errorCarga = false;
    try {
      incentivos = incentivosApiService.obtenerIncentivos(donanteId);
    } catch (BackendNoDisponibleException e) {
      incentivos = IncentivosMapper.soloFallback();
      errorCarga = true;
    }

    model.addAttribute("errorCarga", errorCarga);
    model.addAttribute("categorias", incentivos.categorias());
    model.addAttribute("categoriaActual", incentivos.categoriaActual());
    model.addAttribute("categoriaSiguiente", incentivos.categoriaSiguiente());
    model.addAttribute("misionesFaltantesCategoria", incentivos.misionesFaltantesCategoria());
    model.addAttribute("filtrosCategoriaMision", incentivos.filtrosCategoriaMision());
    model.addAttribute("misiones", incentivos.misiones());
    model.addAttribute("insignias", incentivos.insignias());
    model.addAttribute("insigniasObtenidas", incentivos.insigniasObtenidas());
    model.addAttribute("insigniasTotales", incentivos.insigniasTotales());

    return "personaDonante/incentivos";
  }

  @GetMapping("/mapaDonaciones")
  @PreAuthorize("hasAuthority('VER_MIS_DONACIONES')")
  public String mapaDonaciones(Model model, HttpSession session, @RequestParam(required = false) Long donacionId) {
    model.addAttribute("titulo", "Mapa donaciones");
    model.addAttribute("vistaActual", "mapaDonaciones");

    Long donanteId = resolverDonanteId(session);
    List<DonacionVistaDto> donacionesEnRuta = new ArrayList<>();
    DonacionVistaDto donacionSeleccionada = null;
    if (donanteId != null) {
      try {
        List<DonacionVistaDto> misDonaciones = donacionApiService.obtenerPorDonante(donanteId);
        donacionesEnRuta = misDonaciones.stream()
            .filter(d -> d.estado() != null && (d.estado().equalsIgnoreCase("En traslado") || d.estado().equalsIgnoreCase("EN_TRASLADO") || d.estado().equalsIgnoreCase("En transporte")))
            .toList();

        if (donacionId != null) {
          donacionSeleccionada = misDonaciones.stream()
              .filter(d -> Objects.equals(d.id(), donacionId))
              .findFirst()
              .orElse(null);
        }
        if (donacionSeleccionada == null && !donacionesEnRuta.isEmpty()) {
          donacionSeleccionada = donacionesEnRuta.get(0);
        }
      } catch (Exception ignored) {
      }
    }

    model.addAttribute("donacionesEnRuta", donacionesEnRuta);
    model.addAttribute("donacionEnRuta", donacionSeleccionada);
    boolean viajeActivo = donacionSeleccionada != null && (donacionSeleccionada.destacado() || "En transporte".equalsIgnoreCase(donacionSeleccionada.estado()) || "En traslado".equalsIgnoreCase(donacionSeleccionada.estado()));
    model.addAttribute("viajeActivo", viajeActivo);

    model.addAttribute("pines", List.of(
        new PinMapaDto(1L, "Escuela Primaria 12", "pinTrasladoActivo",
            "En ruta", "25 min", "/personaDonante/entidadesBeneficiarias", true),
        new PinMapaDto(2L, "Escuela Secundaria 8", "pinDestinoEscuela",
            "Destino", null, "/personaDonante/entidadesBeneficiarias", false),
        new PinMapaDto(3L, "Comedor Los Bajitos", "pinEntregadoComedor",
            "Entregado", null, "/personaDonante/entidadesBeneficiarias", false)));

    model.addAttribute("puntosCercanos", List.of(
        new PuntoCercanoDto(1L, "Punto Community Centro", "Av. Corrientes 1200",
            "Lun a Sáb de 9 a 18 hs", true, "En ruta", "25 min"),
        new PuntoCercanoDto(2L, "Biblioteca San Martín", "Av. Santa Fe 890",
            "Lun a Vie de 10 a 19 hs", false, "Disponible", null),
        new PuntoCercanoDto(3L, "Centro Cultural Recoleta", "Av. Independencia 1234",
            "Mar a Dom de 10 a 22 hs", false, "Disponible", null),
        new PuntoCercanoDto(4L, "Casa de la Solidaridad", "Rivadavia 456",
            "Sáb de 11 a 16 hs", false, "Disponible", null)));

    model.addAttribute("tiposPunto", List.of("Todos", "Puntos de acopio", "Comedores", "Escuelas"));
    model.addAttribute("tipoPuntoActivo", "Todos");
    model.addAttribute("origenLat", -34.598500);
    model.addAttribute("origenLng", -58.420100);

    double destLat = -34.617500;
    double destLng = -58.367500;
    String destNombre = "Comedor Los Bajitos";
    if (donacionSeleccionada != null) {
      if (Long.valueOf(2L).equals(donacionSeleccionada.id()) || (donacionSeleccionada.subcategoria() != null && donacionSeleccionada.subcategoria().equalsIgnoreCase("Camperas"))) {
        destLat = -34.601500;
        destLng = -58.452000;
        destNombre = "Merendero Sol Naciente";
      } else if (Long.valueOf(3L).equals(donacionSeleccionada.id()) || (donacionSeleccionada.subcategoria() != null && donacionSeleccionada.subcategoria().toLowerCase().contains("leche"))) {
        destLat = -34.617500;
        destLng = -58.367500;
        destNombre = "Comedor Los Bajitos";
      }
    }
    model.addAttribute("destinoLat", destLat);
    model.addAttribute("destinoLng", destLng);
    model.addAttribute("destinoNombre", destNombre);
    return "personaDonante/mapaDonaciones";
  }

  @GetMapping("/verNecesidadEntidad/{id}")
  @PreAuthorize("hasAuthority('VER_ENTIDADES_Y_NECESIDADES')")
  public String verNecesidadEntidad(@PathVariable Long id, Model model) {
    model.addAttribute("titulo", "Necesidades de la entidad");
    model.addAttribute("vistaActual", "verNecesidadEntidad");
    model.addAttribute("entidadId", id);

    EntidadDetalleDto entidad = null;
    List<CategoriaNecesidadDto> categorias = List.of();
    boolean errorCarga = false;
    boolean noEncontrada = false;
    try {
      EntidadDetalleVistaDto vista = entidadBeneficiariaApiService.obtenerPorId(id);
      entidad = vista.entidad();
      categorias = vista.categorias();
    } catch (NotFoundException e) {
      noEncontrada = true;
    } catch (BackendNoDisponibleException e) {
      errorCarga = true;
    }

    model.addAttribute("entidad", entidad);
    model.addAttribute("categorias", categorias);
    model.addAttribute("errorCarga", errorCarga);
    model.addAttribute("noEncontrada", noEncontrada);

    return "personaDonante/verNecesidadEntidad";
  }
}

package ar.edu.utn.frba.dds.donaciones.main.controllers;


import ar.edu.utn.frba.dds.donaciones.main.dtos.AdministradorRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CasoExitoResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.EntidadesAyudadasResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.sugerenciaMatch.SugerenciaMatchResponse;
import ar.edu.utn.frba.dds.donaciones.main.services.DonacionService;
import ar.edu.utn.frba.dds.donaciones.main.services.EntidadBeneficiariaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donaciones-service/donaciones")
public class DonacionController {

  private final DonacionService donacionService;
  private final EntidadBeneficiariaService entidadBeneficiariaService;
  public DonacionController(DonacionService donacionService, EntidadBeneficiariaService entidadBeneficiariaService) {
    this.donacionService = donacionService;
    this.entidadBeneficiariaService = entidadBeneficiariaService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_DONACIONES')")
  public List<DonacionResponse> obtenerTodasLasDonaciones() {
    return donacionService.obtenerTodas();
  }
  @GetMapping("/{idDonacion}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_DONACIONES')")
  public DonacionResponse obtenerDonacionPorId(@PathVariable Long idDonacion) {
    return donacionService.obtenerDonacionPorId(idDonacion);
  }
  @GetMapping("/casos-exito")
  public List<CasoExitoResponse> obtenerCasosExito() {
    return donacionService.obtenerCasosExito();
  }

  @GetMapping(params = "idNecesidad")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_DONACIONES') " +
      "or @validadorDeAutorizacion.puedeVerNecesidad(#idNecesidad, authentication)")
  public List<DonacionResponse> obtenerDonacionesPorNecesidad(@RequestParam Long idNecesidad) {
    return donacionService.obtenerPorIdNecesidad(idNecesidad);
  }

  @GetMapping("/donante/{idDonante}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_DONACIONES') "  +
      "or @validadorDeAutorizacion.puedeVerDonacionesDeDonante(#idDonante, authentication)")
  public List<DonacionResponse> obtenerDonacionesDeUnDonante(@PathVariable Long idDonante) {
    return donacionService.obtenerDonaciones(idDonante);
  }

  @GetMapping("/donante/{idDonante}/entidades-ayudadas-este-mes")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_DONACIONES') " +
      "or @validadorDeAutorizacion.puedeVerDonacionesDeDonante(#idDonante, authentication)")
  public EntidadesAyudadasResponse contarEntidadesAyudadasEsteMes(@PathVariable Long idDonante) {
    return donacionService.contarEntidadesAyudadasEsteMes(idDonante);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('REGISTRAR_DONACION') " +
      "or @validadorDeAutorizacion.puedeCrearDonacion(#request, authentication)")
  public List<DonacionResponse> crearDonacion(@RequestBody CrearDonacionRequest request) {
    return donacionService.crearDonacion(request);
  }

  @PatchMapping("/{id}/estado")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('CAMBIAR_ESTADO_DONACION') " +
      "or @validadorDeAutorizacion.puedeActualizarDonacion(#request, #idDonacion, authentication) " +
      "or hasRole('LOGISTICA_SERVICE')")
  public DonacionResponse actualizarEstadoDonacion(@PathVariable("id") Long idDonacion, @RequestBody CambiarEstadoRequest request) {
    return this.donacionService.actualizarEstado(idDonacion, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ELIMINAR_DONACION') " +
      "or @validadorDeAutorizacion.puedeEliminarDonacion(#id, authentication)")
  public void eliminarDonacion(@PathVariable Long id) {
    this.donacionService.eliminarDonacion(id);
  }

  @GetMapping("/matcheador")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_MATCHEOS')")
  public List<SugerenciaMatchResponse> mostrarResultadoDeSeleccion(){
    return this.donacionService.sugerenciaDeMatcheoDeDonacion();
  }

  @PostMapping("/matcheador/ejecutar")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_MATCHEOS', 'SELECCIONAR_MATCHEO')")
  public List<SugerenciaMatchResponse> ejecutarMatcheoADemanda() {
    this.donacionService.realizarMatcheoDonaciones();
    return this.donacionService.sugerenciaDeMatcheoDeDonacion();
  }

  @PostMapping("/matcheador/{idMatcheo}/{idNecesidadRecibidora}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('SELECCIONAR_MATCHEO')")
  public void seleccionarEntidadParaDonacion(@PathVariable Long idMatcheo, @PathVariable Long idNecesidadRecibidora, @RequestBody(required = false) AdministradorRequest request){
    if (request == null) {
      request = new AdministradorRequest("Administrador");
    }
    this.donacionService.seleccionarNecesidad(idMatcheo, idNecesidadRecibidora, request);
  }
}

package ar.edu.utn.frba.dds.donaciones.main.controllers;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadDto;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadRequest;
import ar.edu.utn.frba.dds.donaciones.main.services.EntidadBeneficiariaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("donaciones-service/entidades-beneficiarias")
public class EntidadBeneficiariaController {

    private final EntidadBeneficiariaService entidadBeneficiariaService;

    public EntidadBeneficiariaController(EntidadBeneficiariaService entidadBeneficiariaService) {
        this.entidadBeneficiariaService = entidadBeneficiariaService;
    }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_ENTIDADES_BENEFICIARIAS') or hasAnyRole('DONANTE')")
  public List<EntidadBeneficiariaResponse> obtenerTodasLasEntidadesBeneficiarias(
      @RequestParam(required = false) String busqueda,
      @RequestParam(required = false) String nombre,
      @RequestParam(required = false) String ubicacion,
      @RequestParam(required = false) String tipo
  ){
    return this.entidadBeneficiariaService.buscarConFiltros(busqueda, nombre, ubicacion, tipo);
  }

  @GetMapping("/usuario/{idUsuarioAuth}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_ENTIDADES_BENEFICIARIAS') " +
      "or @validadorDeAutorizacion.puedeObtenerEntidadPorUsuario(#idUsuarioAuth, authentication)")
  public EntidadBeneficiariaResponse obtenerEntidadPorUsuarioAuth(@PathVariable Long idUsuarioAuth) {
    return this.entidadBeneficiariaService.buscarPorIdUsuarioAuth(idUsuarioAuth);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_ENTIDADES_BENEFICIARIAS') " +
  "or @validadorDeAutorizacion.puedeVerEntidad(#id, authentication)")
  public EntidadBeneficiariaResponse obtenerEntidadPorId(@PathVariable Long id){
    return this.entidadBeneficiariaService.findById(id);
  }

  @GetMapping("/{id}/necesidades")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_NECESIDADES') " +
  "or @validadorDeAutorizacion.puedeVerNecesidadesDeEntidad(#id, authentication)")
  public List<NecesidadDto> obtenerNecesidadesDeEntidad(@PathVariable Long id){
    return this.entidadBeneficiariaService.findNecesidadesByIdEntidad(id);
  }

  @GetMapping("/{id}/donaciones")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_TODAS_LAS_DONACIONES') " +
  "or @validadorDeAutorizacion.puedeVerDonacionesAEntidad(#id, authentication)")
  public List<DonacionResponse> obtenerDonacionesAEntidad(@PathVariable Long id){
    return this.entidadBeneficiariaService.findDonacionesByIdEntidad(id);
  }
  @GetMapping("/necesidades/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_NECESIDADES') " +
  "or @validadorDeAutorizacion.puedeVerNecesidad(#id, authentication)")
  public NecesidadDto obtenerNecesidadPorId(@PathVariable Long id){
    return this.entidadBeneficiariaService.findNecesidadById(id);
  }
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ACTUALIZAR_ENTIDAD_BENEFICIARIA') " +
  "or @validadorDeAutorizacion.puedeActualizarEntidad(#id,#request, authentication)")
  public EntidadBeneficiariaResponse actualizarEntidadBeneficiaria(@PathVariable Long id, @RequestBody EntidadBeneficiariaRequest request){
    return this.entidadBeneficiariaService.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ELIMINAR_ENTIDAD_BENEFICIARIA')")
  public void eliminarEntidadBeneficiaria(@PathVariable Long id){
    this.entidadBeneficiariaService.eliminar(id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('CREAR_ENTIDAD_BENEFICIARIA')")
  public EntidadBeneficiariaResponse crearNuevaEntidadBeneficiaria(@RequestBody EntidadBeneficiariaRequest request){
    return this.entidadBeneficiariaService.crear(request);
  }

  @PostMapping("/{id}/necesidades")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('CREAR_NECESIDADES') " +
      "or @validadorDeAutorizacion.puedeCrearNecesidad(#id,#request, authentication)")
  public void agregarNecesidad(@PathVariable Long id, @RequestBody NecesidadRequest request){
    this.entidadBeneficiariaService.agregarNecesidad(id, request);
  }

  @DeleteMapping("/{id}/necesidades/{idNecesidad}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ELIMINAR_NECESIDADES')")
  public void eliminarNecesidad(@PathVariable Long id, @PathVariable Long idNecesidad){
    this.entidadBeneficiariaService.eliminarNecesidad(id, idNecesidad);
  }
}

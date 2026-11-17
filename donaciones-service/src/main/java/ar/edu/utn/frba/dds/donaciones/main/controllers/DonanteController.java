package ar.edu.utn.frba.dds.donaciones.main.controllers;


import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteCreateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteUpdateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.IncentivosClient;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donaciones-service/donantes")
public class DonanteController {

  private final DonanteService donanteService;
  private final IncentivosClient incentivosClient;
  private final ar.edu.utn.frba.dds.donaciones.main.services.impl.ImportadorDonantesService importadorDonantesService;

  public DonanteController(
      DonanteService donanteService,
      IncentivosClient incentivosClient,
      ar.edu.utn.frba.dds.donaciones.main.services.impl.ImportadorDonantesService importadorDonantesService) {
    this.incentivosClient = incentivosClient;
    this.donanteService = donanteService;
    this.importadorDonantesService = importadorDonantesService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_DONANTES')")
  public List<DonanteResponse> obtenerTodosLosDonantes(){
    return this.donanteService.obtenerTodos();
  }
  @GetMapping("/todosIncluyendoInactivos")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_DONANTES_INACTIVOS')")
  public List<DonanteResponse> obtenerTodosIncluyendoInactivos(
          @RequestParam(required = false) String buscar,
          @RequestParam(required = false) String tipo,
          @RequestParam(required = false) String estado){
    return this.donanteService.obtenerTodosIncluyendoInactivos(buscar, tipo, estado);
  }


  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_DONANTES') " +
      "or @validadorDeAutorizacion.puedeVerDonante(#id, authentication)")
  public DonanteResponse obtenerDonantePodId(@PathVariable Long id){
    return this.donanteService.buscarPorId(id);
  }

  @GetMapping("/usuario/{idUsuarioAuth}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('VER_DONANTES') " +
  "or @validadorDeAutorizacion.puedeObtenerDonanteConUsuario(#idUsuarioAuth, authentication)")
  public DonanteResponse obtenerDonantePorUsuarioAuth(@PathVariable Long idUsuarioAuth) {
    return this.donanteService.buscarPorIdUsuarioAuth(idUsuarioAuth);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('REGISTRAR_DONANTE')")
  public DonanteResponse agregarDonante(@RequestBody DonanteCreateRequest donanteCreateRequest){
    return this.donanteService.agregarDonante(donanteCreateRequest);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ELIMINAR_DONANTES')")
  public void eliminarDonante(@PathVariable Long id){
    this.donanteService.eliminarDonante(id);
  }

  @PatchMapping("/{id}/baja")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('DAR_DE_BAJA_DONATE')")
  public void darDeBajaDonante(@PathVariable Long id){
    this.donanteService.darDeBajaDonante(id);
  }

  @PatchMapping("/{id}/alta")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('DAR_DE_ALTA_DONATE')")
  public void darDeAltaDonante(@PathVariable Long id){
    this.donanteService.darDeAltaDonante(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('EDITAR_DONANTES') " +
      "or @validadorDeAutorizacion.puedeEditarDonante(#id,#donante, authentication)")
  public DonanteResponse actualizarDonante(@PathVariable Long id ,@RequestBody DonanteUpdateRequest donante){
    //TODO
    return null;
  }

  @PostMapping("/{id}/representantes")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('EDITAR_DONANTES', 'REGISTRAR_DONANTE')")
  public DonanteResponse agregarRepresentante(
      @PathVariable Long id,
      @RequestBody RepresentanteDTO representanteDTO) {
    return this.donanteService.agregarRepresentante(id, representanteDTO);
  }

  @PutMapping("/{id}/representantes/{idRepresentante}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('EDITAR_DONANTES')")
  public DonanteResponse editarRepresentante(
      @PathVariable Long id,
      @PathVariable Long idRepresentante,
      @RequestBody RepresentanteDTO representanteDTO) {
    return this.donanteService.editarRepresentante(id, idRepresentante, representanteDTO);
  }

  @DeleteMapping("/{id}/representantes/{idRepresentante}")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('EDITAR_DONANTES', 'ELIMINAR_DONANTES')")
  public DonanteResponse eliminarRepresentante(
      @PathVariable Long id,
      @PathVariable Long idRepresentante) {
    return this.donanteService.eliminarRepresentante(id, idRepresentante);
  }

  @PostMapping("/importar")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('REGISTRAR_DONANTE')")
  public List<DonanteResponse> importarDonantesCsv(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
    return this.importadorDonantesService.importarYPersistir(file);
  }

}

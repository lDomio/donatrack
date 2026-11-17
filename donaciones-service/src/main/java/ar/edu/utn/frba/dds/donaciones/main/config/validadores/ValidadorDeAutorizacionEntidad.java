package ar.edu.utn.frba.dds.donaciones.main.config.validadores;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteUpdateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadRequest;
import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import ar.edu.utn.frba.dds.donaciones.main.services.EntidadBeneficiariaService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ValidadorDeAutorizacionEntidad implements ValidadorDeAutorizacion{
  private final EntidadBeneficiariaService entidadBeneficiariaService;
  private String rolQueTrata = "ROLE_ENTIDAD_BENEFICIARIA";

  public ValidadorDeAutorizacionEntidad(EntidadBeneficiariaService entidadBeneficiariaService, DonanteService donanteService) {
    this.entidadBeneficiariaService = entidadBeneficiariaService;
  }
  @Override
  public boolean trataRol(String rol) {
    return ValidadorDeAutorizacionGeneral.formatearRol(rol).equals(rolQueTrata);
  }
  @Override
  public boolean puedeVerNecesidad(Long idNecesidad, Authentication auth){
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("VER_MIS_NECESIDADES", auth)) {
      throw new AccessDeniedException("No tiene permiso para VER_MIS_NECESIDADES");
    }
    Usuario usuario = (Usuario) auth.getPrincipal();
    if (!entidadBeneficiariaService.validarNecesidadDeUsuario(idNecesidad, usuario.idUsuarioAuth())) {
      throw new AccessDeniedException("Acceso denegado: No puede ver una necesidad que no es suya.");
    }
    System.out.println("Autorizo");
    return true;
  }
  @Override
  public boolean puedeActualizarDonacion(CambiarEstadoRequest estadoRequest, Long idDonacion, Authentication auth)  {
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("CONFIRMAR_RECEPCION_DE_DONACION_ASIGNADA", auth)) {
      throw new AccessDeniedException("No tiene permiso para CONFIRMAR_RECEPCION_DE_DONACION_ASIGNADA");
    }
      Usuario usuario = (Usuario) auth.getPrincipal();
    if(!entidadBeneficiariaService.validarConfirmacionDeDonacion( estadoRequest, idDonacion, usuario.idUsuarioAuth())){
      throw new AccessDeniedException("Acceso denegado: No puede actualizar una donacion que no tiene asignada.");
    }
    return true;
  }

  @Override
  public boolean puedeVerDonacionesDeDonante(Long idDonante, Authentication auth) {
    return false;
  }

  @Override
  public boolean puedeCrearDonacion(CrearDonacionRequest donacionRequest, Authentication auth) {
    return false;
  }

  @Override
  public boolean puedeEliminarDonacion(Long idDonacion, Authentication authentication) {
    return false;
  }

  @Override
  public boolean puedeObtenerEntidadPorUsuario(Long idUsuarioAuth, Authentication authentication) {
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("VER_MIS_DATOS", authentication)) {
      System.out.println("No tiene permiso " + idUsuarioAuth);
      throw new AccessDeniedException("No tiene permiso para VER_MIS_DATOS");
    }
    System.out.println("Pido el id " + idUsuarioAuth);
    if(!((Usuario) authentication.getPrincipal()).idUsuarioAuth().equals(idUsuarioAuth)){
      throw new AccessDeniedException("Acceso denegado: No puede obtener la entidad de otro usuario.");
    }
    return true;
  }

  @Override
  public boolean puedeVerEntidad(Long idEntidad, Authentication authentication)  {
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("VER_MIS_DATOS", authentication)) {
      throw new AccessDeniedException("No tiene permiso para VER_MIS_DATOS");
    }
    Usuario usuario = (Usuario) authentication.getPrincipal();
    if(!entidadBeneficiariaService.validarEntidadDeUsuario(idEntidad, usuario.idUsuarioAuth())){
      throw new AccessDeniedException("Acceso denegado: No puede ver los datos de otra entidad.");
    }
    return true;
  }

  @Override
  public boolean puedeVerNecesidadesDeEntidad(Long idEntidad, Authentication authentication)  {
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("VER_MIS_NECESIDADES", authentication)) {
      throw new AccessDeniedException("No tiene permiso para VER_MIS_NECESIDADES");
    }
    Usuario usuario = (Usuario) authentication.getPrincipal();
    if(!entidadBeneficiariaService.validarEntidadDeUsuario(idEntidad, usuario.idUsuarioAuth())){
      throw new AccessDeniedException("Acceso denegado: No puede ver las necesidades de otra entidad.");
    }
    return true;
  }

  @Override
  public boolean puedeVerDonacionesAEntidad(Long idEntidad, Authentication authentication) {
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("VER_DONACIONES_ASIGNADAS", authentication)) {
      throw new AccessDeniedException("No tiene permiso para VER_DONACIONES_ASIGNADAS");
    }
    Usuario usuario = (Usuario) authentication.getPrincipal();
    if(!entidadBeneficiariaService.validarEntidadDeUsuario(idEntidad, usuario.idUsuarioAuth())){
      throw new AccessDeniedException("Acceso denegado: No puede ver las donaciones asignadas a otra entidad.");
    }
    return true;
  }

  @Override
  public boolean puedeActualizarEntidad(Long idEntidad, EntidadBeneficiariaRequest entidadActualizada, Authentication authentication) {
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("ACTUALIZAR_MIS_DATOS", authentication)) {
      throw new AccessDeniedException("No tiene permiso para ACTUALIZAR_MIS_DATOS");
    }
    Usuario usuario = (Usuario) authentication.getPrincipal();
    if(!entidadBeneficiariaService.validarEntidadDeUsuario(idEntidad, usuario.idUsuarioAuth())){
      throw new AccessDeniedException("Acceso denegado: No puede actualizar otra entidad.");
    }
    return true;
  }

  @Override
  public boolean puedeCrearNecesidad(Long idEntidad, NecesidadRequest necesidadRequest, Authentication authentication) {
    if (!ValidadorDeAutorizacionGeneral.tienePermiso("CREAR_NECESIDAD_MIA", authentication)) {
      throw new AccessDeniedException("No tiene permiso para CREAR_NECESIDAD_MIA");
    }
    Usuario usuario = (Usuario) authentication.getPrincipal();
    if(!entidadBeneficiariaService.validarEntidadDeUsuario(idEntidad, usuario.idUsuarioAuth())){
      throw new AccessDeniedException("Acceso denegado: No puede crear necesidades para otra entidad.");
    }
    return true;
  }

  @Override
  public boolean puedeVerDonante(Long idDonante, Authentication authentication) {
    return false;
  }

  @Override
  public boolean puedeObtenerDonanteConUsuario(Long idUsuarioAuth, Authentication authentication) {
    return false;
  }

  @Override
  public boolean puedeEditarDonante(Long idDonante, DonanteUpdateRequest donanteActualizado, Authentication authentication) {
    return false;
  }
}

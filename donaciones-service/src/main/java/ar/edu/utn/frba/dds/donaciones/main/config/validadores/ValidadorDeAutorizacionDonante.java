package ar.edu.utn.frba.dds.donaciones.main.config.validadores;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteUpdateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadRequest;
import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ValidadorDeAutorizacionDonante implements ValidadorDeAutorizacion{
  private final DonanteService donanteService;
  private String rolQueTrata = "ROLE_DONANTE";


  public ValidadorDeAutorizacionDonante(DonanteService donanteService) {
    this.donanteService = donanteService;
  }

  public boolean trataRol(String rol) {
    return ValidadorDeAutorizacionGeneral.formatearRol(rol).equals(rolQueTrata);
  }

  @Override
  public boolean puedeVerNecesidad(Long idNecesidad, Authentication auth) {
    return true;
  }

  @Override
  public boolean puedeActualizarDonacion(CambiarEstadoRequest estadoRequest, Long idDonacion, Authentication auth) {
    return false;
  }

  public boolean puedeVerDonacionesDeDonante(Long idDonante, Authentication auth){
    if (ValidadorDeAutorizacionGeneral.tienePermiso("VER_MIS_DONACIONES", auth)){
      Usuario usuario = (Usuario) auth.getPrincipal();
      return donanteService.validarUsuarioDeDonante(idDonante, usuario.idUsuarioAuth());
    }
    return false;
  }

  public boolean puedeCrearDonacion(CrearDonacionRequest donacionRequest, Authentication auth){
    return false;
  }

  @Override
  public boolean puedeEliminarDonacion(Long idDonacion, Authentication authentication) {
    return false;
  }

  @Override
  public boolean puedeObtenerEntidadPorUsuario(Long idUsuarioAuth, Authentication authentication) {
    return false;
  }

  @Override
  public boolean puedeVerEntidad(Long idEntidad, Authentication authentication) {
    return ValidadorDeAutorizacionGeneral.tienePermiso("VER_ENTIDADES_Y_NECESIDADES", authentication);
  }

  @Override
  public boolean puedeVerNecesidadesDeEntidad(Long idEntidad, Authentication authentication) {
    return ValidadorDeAutorizacionGeneral.tienePermiso("VER_ENTIDADES_Y_NECESIDADES", authentication);
  }

  @Override
  public boolean puedeVerDonacionesAEntidad(Long idEntidad, Authentication authentication) {
    return ValidadorDeAutorizacionGeneral.tienePermiso("VER_ENTIDADES_Y_NECESIDADES", authentication);
  }

  @Override
  public boolean puedeActualizarEntidad(Long idEntidad, EntidadBeneficiariaRequest entidadActualizada, Authentication authentication) {
    return false;
  }

  @Override
  public boolean puedeCrearNecesidad(Long idEntidad, NecesidadRequest necesidadRequest, Authentication authentication) {
    return false;
  }

  @Override
  public boolean puedeVerDonante(Long idDonante, Authentication authentication) {
    if (ValidadorDeAutorizacionGeneral.tienePermiso("VER_MIS_DATOS", authentication)){
      Usuario usuario = (Usuario) authentication.getPrincipal();
      return donanteService.validarUsuarioDeDonante(idDonante, usuario.idUsuarioAuth());
    }
    return false;
  }

  @Override
  public boolean puedeObtenerDonanteConUsuario(Long idUsuarioAuth, Authentication authentication) {
    if (ValidadorDeAutorizacionGeneral.tienePermiso("VER_MIS_DATOS", authentication)) {
      Usuario usuario = (Usuario) authentication.getPrincipal();
      return Objects.equals(idUsuarioAuth, usuario.idUsuarioAuth());
    }
    return false;
  }

  @Override
  public boolean puedeEditarDonante(Long idDonante, DonanteUpdateRequest donanteActualizado, Authentication authentication) {
    if (ValidadorDeAutorizacionGeneral.tienePermiso("ACTUALIZAR_MIS_DATOS", authentication)){
      Usuario usuario = (Usuario) authentication.getPrincipal();
      return donanteService.validarUsuarioDeDonante(idDonante, usuario.idUsuarioAuth());
    }
    return false;
  }
}

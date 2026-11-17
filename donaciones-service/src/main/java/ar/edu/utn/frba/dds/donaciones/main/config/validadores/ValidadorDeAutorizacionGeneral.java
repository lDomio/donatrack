package ar.edu.utn.frba.dds.donaciones.main.config.validadores;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteUpdateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadRequest;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("validadorDeAutorizacion")
public class ValidadorDeAutorizacionGeneral {
  private final List<ValidadorDeAutorizacion> validadores;

  public ValidadorDeAutorizacionGeneral(List<ValidadorDeAutorizacion> validadores) {
    this.validadores = validadores;
  }
  public boolean puedeVerNecesidad(Long idNecesidad, Authentication auth) {
    return evaluar(auth, v -> v.puedeVerNecesidad(idNecesidad, auth));
  }

  public boolean puedeActualizarDonacion(CambiarEstadoRequest estadoRequest, Long idDonacion, Authentication auth) {
    return evaluar(auth, v -> v.puedeActualizarDonacion(estadoRequest, idDonacion, auth));
  }

  public boolean puedeVerDonacionesDeDonante(Long idDonante, Authentication auth) {
    return evaluar(auth, v -> v.puedeVerDonacionesDeDonante(idDonante, auth));
  }

  public boolean puedeCrearDonacion(CrearDonacionRequest donacionRequest, Authentication auth) {
    return evaluar(auth, v -> v.puedeCrearDonacion(donacionRequest, auth));
  }
  public boolean puedeEliminarDonacion(Long idDonacion,Authentication authentication){
    return evaluar(authentication, v -> v.puedeEliminarDonacion(idDonacion, authentication));

  }
  public boolean puedeObtenerEntidadPorUsuario(Long idUsuarioAuth, Authentication authentication){
    return evaluar(authentication, v -> v.puedeObtenerEntidadPorUsuario(idUsuarioAuth, authentication));
  }
  public boolean puedeVerEntidad(Long idEntidad, Authentication authentication){
    return evaluar(authentication, v -> v.puedeVerEntidad(idEntidad, authentication));
  }
  public boolean puedeVerNecesidadesDeEntidad(Long idEntidad, Authentication authentication){
    return evaluar(authentication, v -> v.puedeVerNecesidadesDeEntidad(idEntidad, authentication));
  }
  public boolean puedeVerDonacionesAEntidad(Long idEntidad, Authentication authentication){
    return evaluar(authentication, v -> v.puedeVerDonacionesAEntidad(idEntidad, authentication));
  }
  public boolean puedeActualizarEntidad(Long idEntidad, EntidadBeneficiariaRequest entidadActualizada, Authentication authentication){
    return evaluar(authentication, v -> v.puedeActualizarEntidad(idEntidad,entidadActualizada, authentication));
  }
  public boolean puedeCrearNecesidad(Long idEntidad, NecesidadRequest necesidadRequest, Authentication authentication){
    return evaluar(authentication, v -> v.puedeCrearNecesidad(idEntidad, necesidadRequest,authentication));
  }
  public boolean puedeVerDonante(Long idDonante,Authentication authentication){
    return evaluar(authentication, v -> v.puedeVerDonante(idDonante, authentication));
  }
  public boolean puedeObtenerDonanteConUsuario(Long idUsuarioAuth, Authentication authentication){
    return evaluar(authentication, v -> v.puedeObtenerDonanteConUsuario(idUsuarioAuth, authentication));
  }
  public boolean puedeEditarDonante(Long idDonante, DonanteUpdateRequest donanteActualizado, Authentication authentication){
    return evaluar(authentication, v -> v.puedeEditarDonante(idDonante, donanteActualizado, authentication));
  }

  private boolean evaluar(Authentication auth, Predicate<ValidadorDeAutorizacion> operacion) {
    List<String> roles = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority) // Más seguro e idiomático que Object::toString
        .filter(authority -> authority.startsWith("ROLE_"))
        .toList();

    return validadores.stream()
        .filter(v -> roles.stream().anyMatch(v::trataRol))
        .anyMatch(operacion);
  }

  public static boolean tienePermiso(String permiso, Authentication auth){
    System.out.println(auth.getAuthorities());
    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(permiso));
  }

  public static String formatearRol(String rol){
    if (!rol.startsWith("ROLE_")){
      return "ROLE_" + rol;
    } else {
      return rol;
    }
  }
}

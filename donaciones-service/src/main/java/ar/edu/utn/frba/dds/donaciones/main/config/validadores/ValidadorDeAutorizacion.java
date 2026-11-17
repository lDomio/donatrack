package ar.edu.utn.frba.dds.donaciones.main.config.validadores;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteUpdateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadRequest;
import java.nio.file.AccessDeniedException;
import org.springframework.security.core.Authentication;

public interface ValidadorDeAutorizacion {
  public boolean trataRol(String rol);
  public boolean puedeVerNecesidad(Long idNecesidad, Authentication auth);
  public boolean puedeActualizarDonacion(CambiarEstadoRequest estadoRequest, Long idDonacion, Authentication auth);
  public boolean puedeVerDonacionesDeDonante(Long idDonante, Authentication auth);
  public boolean puedeCrearDonacion(CrearDonacionRequest donacionRequest, Authentication auth);
  public boolean puedeEliminarDonacion(Long idDonacion,Authentication authentication);
  public boolean puedeObtenerEntidadPorUsuario(Long idUsuarioAuth, Authentication authentication);
  public boolean puedeVerEntidad(Long idEntidad, Authentication authentication);
  public boolean puedeVerNecesidadesDeEntidad(Long idEntidad, Authentication authentication);
  public boolean puedeVerDonacionesAEntidad(Long idEntidad, Authentication authentication);
  public boolean puedeActualizarEntidad(Long idEntidad, EntidadBeneficiariaRequest entidadActualizada, Authentication authentication);
  public boolean puedeCrearNecesidad(Long idEntidad, NecesidadRequest necesidadRequest, Authentication authentication);
  public boolean puedeVerDonante(Long idDonante,Authentication authentication);
  public boolean puedeObtenerDonanteConUsuario(Long idUsuarioAuth, Authentication authentication);
  public boolean puedeEditarDonante(Long idDonante, DonanteUpdateRequest donanteActualizado, Authentication authentication);
}

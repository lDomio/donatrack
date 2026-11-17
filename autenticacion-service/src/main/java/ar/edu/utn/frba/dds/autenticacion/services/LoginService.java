package ar.edu.utn.frba.dds.autenticacion.services;

import ar.edu.utn.frba.dds.autenticacion.dto.RegistroUsuarioDTO;
import ar.edu.utn.frba.dds.autenticacion.dto.UserRolesPermissionsDTO;
import ar.edu.utn.frba.dds.autenticacion.dto.UsuarioResponseDTO;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.Permisos;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.Rol;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.TipoRol;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.Usuario;
import ar.edu.utn.frba.dds.autenticacion.repositories.PermisosRepository;
import ar.edu.utn.frba.dds.autenticacion.repositories.RolRepository;
import ar.edu.utn.frba.dds.autenticacion.repositories.UsuariosRepository;
import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.common.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService {
  private final UsuariosRepository usuariosRepository;
  private final RolRepository rolRepository;
  private final PermisosRepository permisosRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public LoginService(
      UsuariosRepository usuariosRepository,
      RolRepository rolRepository,
      PermisosRepository permisosRepository,
      JwtUtil jwtUtil) {
    this.usuariosRepository = usuariosRepository;
    this.rolRepository = rolRepository;
    this.permisosRepository = permisosRepository;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public Usuario autenticarUsuario(String username, String password) {
    Usuario usuario = usuariosRepository.findByNombreDeUsuario(username)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

    // Verificar la contraseña usando BCrypt
    if (!passwordEncoder.matches(password, usuario.getContrasenia())) {
      throw new ResourceNotFoundException("Credenciales inválidas para: " + username);
    }

    return usuario;
  }

  public String generarAccessToken(Usuario usuario) {
    List<String> permisos = (usuario.getRol() != null && usuario.getRol().getPermisos() != null)
        ? usuario.getRol().getPermisos().stream().map(Permisos::getPermiso).toList()
        : List.of();

    String rol = usuario.getRol() != null && usuario.getRol().getTipoRol() != null
        ? usuario.getRol().getTipoRol().name()
        : "";

    return jwtUtil.generarAccessToken(usuario.getId(), usuario.getNombreDeUsuario(), rol, permisos);
  }

  public String generarRefreshToken(String username) {
    return jwtUtil.generarRefreshToken(username);
  }

  /**
   * Regenera el access token a partir del username validado en un refresh token,
   * releyendo el usuario para que el rol y los permisos viajen siempre actualizados.
   */
  public String generarAccessTokenPorUsername(String username) {
    Usuario usuario = usuariosRepository.findByNombreDeUsuario(username)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

    return generarAccessToken(usuario);
  }

  public UserRolesPermissionsDTO obtenerRolesYPermisosUsuario(String username) {
    Usuario usuario = usuariosRepository.findByNombreDeUsuario(username)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

    return UserRolesPermissionsDTO.builder()
        .username(usuario.getNombreDeUsuario())
        .rol(usuario.getRol().getTipoRol())
        .permisos(usuario.getRol().getPermisos())
        .build();
  }

  public UsuarioResponseDTO registrarUsuario(RegistroUsuarioDTO dto) {
    if (usuariosRepository.findByNombreDeUsuario(dto.username()).isPresent()) {
      throw new IllegalArgumentException("El nombre de usuario '" + dto.username() + "' ya está registrado");
    }

    TipoRol rolTmp = TipoRol.DONANTE;
    if (dto.rol() != null && !dto.rol().isBlank()) {
      try {
        rolTmp = TipoRol.valueOf(dto.rol().toUpperCase());
      } catch (IllegalArgumentException ignored) {
        // default a DONANTE
      }
    }
    final TipoRol tipoRol = rolTmp;

    List<String> nombresPermisos = switch (tipoRol) {
      case ADMINISTRADOR -> List.of(
          "REGISTRAR_DONANTE",
          "REGISTRAR_DONACION",
          "VER_RANKING",
          "GESTIONAR_CAMIONES",
          "CREAR_CATEGORIAS",
          "ACTUALIZAR_CATEGORIAS",
          "ELIMINAR_CATEGORIAS",
          "VER_TODAS_LAS_DONACIONES",
          "CAMBIAR_ESTADO_DONACION",
          "ELIMINAR_DONACION",
          "VER_MATCHEOS",
          "SELECCIONAR_MATCHEO",
          "CREAR_SUBCATEGORIAS",
          "ACTUALIZAR_SUBCATEGORIAS",
          "ELIMINAR_SUBCATEGORIAS",
          "VER_TODAS_LAS_ENTIDADES_BENEFICIARIAS",
          "VER_NECESIDADES",
          "ACTUALIZAR_ENTIDAD_BENEFICIARIA",
          "VER_DONANTES",
          "VER_DONANTES_INACTIVOS",
          "ELIMINAR_DONANTES",
          "EDITAR_DONANTES",
          "ELIMINAR_ENTIDAD_BENEFICIARIA",
          "CREAR_ENTIDAD_BENEFICIARIA",
          "CREAR_NECESIDADES",
          "ELIMINAR_NECESIDADES",
          "DAR_DE_BAJA_DONATE",
          "DAR_DE_ALTA_DONATE"
      );
      case DONANTE -> List.of(
          "VER_MIS_DATOS",
          "VER_ENTIDADES_Y_NECESIDADES",
          "ACTUALIZAR_MIS_DATOS",
          "VER_MIS_DONACIONES",
          "VER_MIS_INCENTIVOS",
          "ELIMINAR_SU_DONACION"
      );
      case ENTIDAD_BENEFICIARIA -> List.of(
          "VER_MIS_DATOS",
          "ACTUALIZAR_MIS_DATOS",
          "VER_MIS_NECESIDADES",
          "CREAR_NECESIDAD_MIA",
          "VER_DONACIONES_ASIGNADAS",
          "CONFIRMAR_RECEPCION_DE_DONACION_ASIGNADA"
      );
      case CAMIONERO -> List.of(
          "INICIAR_VIAJE",
          "MODIFICAR_ESTADO_CAMION",
          "VER_MI_RUTA",
          "VER_MI_CAMION",
          "CANCELAR_MI_ENTREGA"
      );
      default -> List.of();
    };

    Rol rol = rolRepository.findByTipoRol(tipoRol)
        .orElseGet(() -> {
          List<Permisos> perms = new ArrayList<>();
          for (String p : nombresPermisos) {
            perms.add(permisosRepository.findByPermiso(p)
                .orElseGet(() -> permisosRepository.save(new Permisos(null, p))));
          }
          return rolRepository.save(new Rol(null, tipoRol, perms));
        });

    String nombre = (dto.nombre() != null && !dto.nombre().isBlank()) ? dto.nombre() : dto.username();
    Usuario nuevoUsuario = new Usuario(null, rol, nombre, dto.username(), passwordEncoder.encode(dto.password()));

    usuariosRepository.guardar(nuevoUsuario);

    return new UsuarioResponseDTO(nuevoUsuario.getId(), nuevoUsuario.getNombreDeUsuario(), tipoRol.name());
  }
}

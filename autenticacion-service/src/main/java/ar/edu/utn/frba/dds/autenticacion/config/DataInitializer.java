package ar.edu.utn.frba.dds.autenticacion.config;

import ar.edu.utn.frba.dds.autenticacion.modelEntities.Permisos;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.Rol;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.TipoRol;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.Usuario;
import ar.edu.utn.frba.dds.autenticacion.repositories.PermisosRepository;
import ar.edu.utn.frba.dds.autenticacion.repositories.RolRepository;
import ar.edu.utn.frba.dds.autenticacion.repositories.UsuariosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

  private final UsuariosRepository usuariosRepository;
  private final RolRepository rolRepository;
  private final PermisosRepository permisosRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public DataInitializer(
      UsuariosRepository usuariosRepository,
      RolRepository rolRepository,
      PermisosRepository permisosRepository) {
    this.usuariosRepository = usuariosRepository;
    this.rolRepository = rolRepository;
    this.permisosRepository = permisosRepository;
  }

  @Override
  @Transactional
  public void run(String... args) {
    log.info("Verificando precarga de datos de autenticación (roles, permisos, usuarios)...");

    // 1. Rol ADMINISTRADOR y permisos
    Rol rolAdmin = getOrCreateRol(TipoRol.ADMINISTRADOR, List.of(
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
    ));

    // 2. Rol DONANTE y permisos
    Rol rolDonante = getOrCreateRol(TipoRol.DONANTE, List.of(
        "VER_MIS_DATOS",
        "ACTUALIZAR_MIS_DATOS",
        "VER_ENTIDADES_Y_NECESIDADES",
        "VER_MIS_DONACIONES",
        "VER_MIS_INCENTIVOS",
        "ELIMINAR_SU_DONACION"
    ));

    // 3. Rol ENTIDAD_BENEFICIARIA y permisos
    Rol rolBeneficiaria = getOrCreateRol(TipoRol.ENTIDAD_BENEFICIARIA, List.of(
        "VER_MIS_DATOS",
        "ACTUALIZAR_MIS_DATOS",
        "VER_MIS_NECESIDADES",
        "CREAR_NECESIDAD_MIA",
        "VER_DONACIONES_ASIGNADAS",
        "CONFIRMAR_RECEPCION_DE_DONACION_ASIGNADA"
    ));

    // 4. Rol CAMIONERO y permisos
    Rol rolCamionero = getOrCreateRol(TipoRol.CAMIONERO, List.of(
        "INICIAR_VIAJE",
        "MODIFICAR_ESTADO_CAMION",
        "VER_MI_RUTA",
        "VER_MI_CAMION",
        "CANCELAR_MI_ENTREGA"
    ));

    // 5. Usuario Administrador (user: "admin", pass: "admin1234")
    if (usuariosRepository.findByNombreDeUsuario("admin").isEmpty()) {
      Usuario admin = new Usuario(null, rolAdmin, "Administrador General", "admin", passwordEncoder.encode("admin1234"));
      usuariosRepository.save(admin);
      log.info("Usuario inicial 'admin' creado en MySQL con ID: {}", admin.getId());
    }

    // 6. Usuario Donante (user: "donante_juan", pass: "1234")
    if (usuariosRepository.findByNombreDeUsuario("donante_juan").isEmpty()) {
      Usuario donante = new Usuario(null, rolDonante, "Juan Perez", "donante_juan", passwordEncoder.encode("1234"));
      usuariosRepository.save(donante);
      log.info("Usuario inicial 'donante_juan' creado en MySQL con ID: {}", donante.getId());
    }

    // 7. Usuario Entidad Beneficiaria 1 (user: "comedor_sol", pass: "1234") -> Representa a Comedor El Sol
    if (usuariosRepository.findByNombreDeUsuario("comedor_sol").isEmpty()) {
      Usuario beneficiaria = new Usuario(null, rolBeneficiaria, "Comedor El Sol", "comedor_sol", passwordEncoder.encode("1234"));
      usuariosRepository.save(beneficiaria);
      log.info("Usuario inicial 'comedor_sol' creado en MySQL con ID: {}", beneficiaria.getId());
    }

    // 7b. Usuario Entidad Beneficiaria 2 (user: "merendero_sol", pass: "1234")
    if (usuariosRepository.findByNombreDeUsuario("merendero_sol").isEmpty()) {
      Usuario merendero = new Usuario(null, rolBeneficiaria, "Merendero Sol Naciente", "merendero_sol", passwordEncoder.encode("1234"));
      usuariosRepository.save(merendero);
      log.info("Usuario inicial 'merendero_sol' creado en MySQL con ID: {}", merendero.getId());
    }

    // 8. Usuario Chofer 1 (user: "chofer_juan", pass: "1234")
    if (usuariosRepository.findByNombreDeUsuario("chofer_juan").isEmpty()) {
      Usuario chofer = new Usuario(null, rolCamionero, "Juan Chofer", "chofer_juan", passwordEncoder.encode("1234"));
      usuariosRepository.save(chofer);
      log.info("Usuario inicial 'chofer_juan' creado en MySQL con ID: {}", chofer.getId());
    }

    // 9. Usuario Chofer 2 (user: "chofer_ana", pass: "1234")
    if (usuariosRepository.findByNombreDeUsuario("chofer_ana").isEmpty()) {
      Usuario choferAna = new Usuario(null, rolCamionero, "Ana García", "chofer_ana", passwordEncoder.encode("1234"));
      usuariosRepository.save(choferAna);
      log.info("Usuario inicial 'chofer_ana' creado en MySQL con ID: {}", choferAna.getId());
    }
  }

  private Permisos getOrCreatePermiso(String nombre) {
    return permisosRepository.findByPermiso(nombre)
        .orElseGet(() -> permisosRepository.save(new Permisos(null, nombre)));
  }

  private Rol getOrCreateRol(TipoRol tipoRol, List<String> nombresPermisos) {
    return rolRepository.findByTipoRol(tipoRol)
        .orElseGet(() -> {
          List<Permisos> permisos = new ArrayList<>();
          for (String nombre : nombresPermisos) {
            permisos.add(getOrCreatePermiso(nombre));
          }
          return rolRepository.save(new Rol(null, tipoRol, permisos));
        });
  }
}

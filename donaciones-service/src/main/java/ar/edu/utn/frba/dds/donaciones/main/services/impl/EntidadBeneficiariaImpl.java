package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.BienResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.common.exceptions.BusinessException;
import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaSimpleResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.MedioDeContactoDTO;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.*;
import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;
import ar.edu.utn.frba.dds.donaciones.main.exceptions.UnauthorizedException;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Bien;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Representante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.TipoEntidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.EstadoDeUnaDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Direccion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Periodo;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadEnum;
import ar.edu.utn.frba.dds.donaciones.main.repositories.EntidadBeneficiariaRepository;
import ar.edu.utn.frba.dds.donaciones.main.repositories.NecesidadRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.DonacionService;
import ar.edu.utn.frba.dds.donaciones.main.services.EntidadBeneficiariaService;
import ar.edu.utn.frba.dds.donaciones.main.services.SubcategoriaService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EntidadBeneficiariaImpl implements EntidadBeneficiariaService {
    private final EntidadBeneficiariaRepository entidadBeneficiariaRepository;
    private final NecesidadRepository necesidadRepository;
    private final SubcategoriaService subcategoriaService;
    private final Map<String, TipoNecesidad> tiposNecesidad;
    private final ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial generadorId = new ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial();

    public EntidadBeneficiariaImpl(EntidadBeneficiariaRepository entidadBeneficiariaRepository,
                                   NecesidadRepository necesidadRepository,
                                   SubcategoriaService subcategoriaService,
                                   Map<String, TipoNecesidad> tiposNecesidad) {
        this.entidadBeneficiariaRepository = entidadBeneficiariaRepository;
        this.necesidadRepository = necesidadRepository;
        this.subcategoriaService = subcategoriaService;
        this.tiposNecesidad = tiposNecesidad;
    }

    private static boolean esBlanco(String s) {
        return s == null || s.isBlank();
    }

    private static String nuloSiBlanco(String s) {
        return esBlanco(s) ? null : s.trim();
    }

    /**
     * Escapa los comodines de LIKE para que el texto del usuario se busque
     * literal (igual que el contains anterior). La query declara ESCAPE '\'.
     */
    private static String escaparLike(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * Mayusculas sin acentos para comparar sin importar case/acentos.
     */
    private static String normalizar(String s) {
        String sinAcentos = Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toUpperCase(Locale.ROOT);
    }

    /**
     * Mapea el vocabulario del front (Todas, Educacion, Comedores, Salud,
     * Refugios, ONG...) al codigo canonico guardado en TipoEntidad.
     * Devuelve null cuando no hay que filtrar; lanza BusinessException (400)
     * ante un valor desconocido.
     */
    private static String normalizarTipo(String tipo) {
        if (esBlanco(tipo) || normalizar(tipo).equals("TODAS") || normalizar(tipo).equals("TODOS")) {
            return null;
        }
        return switch (normalizar(tipo)) {
            case "COMEDOR", "COMEDORES" -> "COMEDOR";
            case "EDUCACION" -> "EDUCACION";
            case "SALUD" -> "SALUD";
            case "REFUGIO", "REFUGIOS" -> "REFUGIO";
            case "ONG" -> "ONG";
            default -> throw new BusinessException(
                    "Tipo de entidad desconocido: '" + tipo.trim() + "'. Valores válidos: Todas, Educación, Comedores, Salud, Refugios, ONG");
        };
    }

    @Override
    public List<EntidadBeneficiariaResponse> obtenerTodas() {
        return entidadBeneficiariaRepository.findAll().stream()
                .map(this::mapearEntidadResponse)
                .collect(Collectors.toList());
    }

    /**
     * Listado con filtros opcionales resueltos en la DB. Si viene `busqueda`
     * (no blank) se ignoran `nombre`/`ubicacion` para evitar AND
     * contradictorios; un param ausente o en blanco (o "Todas" para tipo) no
     * filtra. Sin params equivale a obtenerTodas().
     */
    @Override
    public List<EntidadBeneficiariaResponse> buscarConFiltros(String busqueda, String nombre, String ubicacion, String tipo) {
        String tipoCanonico = normalizarTipo(tipo);
        String b = nuloSiBlanco(busqueda);
        String n = (b != null) ? null : nuloSiBlanco(nombre);
        String u = (b != null) ? null : nuloSiBlanco(ubicacion);
        return entidadBeneficiariaRepository
                .buscarConFiltros(escaparLike(b), escaparLike(n), escaparLike(u), tipoCanonico)
                .stream()
                .map(this::mapearEntidadResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EntidadBeneficiariaResponse findById(Long id) {
        return this.mapearEntidadResponse(this.obtenerEntidad(id));
    }

  @Override
  public EntidadBeneficiariaResponse buscarPorIdUsuarioAuth(Long idUsuarioAuth) {
    EntidadBeneficiaria entidad = entidadBeneficiariaRepository.findByIdUsuarioAuth(idUsuarioAuth)
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró donante para el usuario de autenticación con id " + idUsuarioAuth));
    return this.mapearEntidadResponse(entidad);
  }

  @Override
  public Boolean validarNecesidadDeUsuario(Long idNecesidad, Long idUsuarioAuth) {
    EntidadBeneficiaria entidad = entidadBeneficiariaRepository.findByIdUsuarioAuth(idUsuarioAuth).orElseThrow(()-> new ResourceNotFoundException("No hay entidad beneficiaria para este usuario"));
    System.out.println("Las necesidades de la entidad son "+ entidad.getNecesidades().stream().map(Necesidad::getId));
    return entidad.getNecesidades().stream().anyMatch(n -> Objects.equals(n.getId(), idNecesidad));
  }

  @Override
  public Boolean validarConfirmacionDeDonacion(CambiarEstadoRequest estadoRequest, Long idDonacion, Long idUsuarioAuth) {
    EntidadBeneficiaria entidad = entidadBeneficiariaRepository.findByIdUsuarioAuth(idUsuarioAuth).orElseThrow(()-> new ResourceNotFoundException("No hay entidad beneficiaria para este usuario"));
    return entidad.getDonacionesAsignadas().stream().anyMatch(d -> Objects.equals(d.getId(), idDonacion)) && estadoRequest.nuevoEstado().equals(TipoDeEstado.ENTREGADA.name());
  }

  @Override
  public Boolean validarEntidadDeUsuario(Long idEntidad, Long idUsuarioAuth) {
    EntidadBeneficiaria entidad = entidadBeneficiariaRepository.findByIdUsuarioAuth(idUsuarioAuth).orElseThrow(()-> new ResourceNotFoundException("No hay entidad beneficiaria para este usuario"));
    return Objects.equals(entidad.getIdUsuarioAuth(), idUsuarioAuth);
  }

  @Override
  public List<NecesidadDto> findNecesidadesByIdEntidad(Long idEntidad){
    return this.obtenerEntidad(idEntidad).getNecesidades().stream().map(this::mapearNecesidad).toList();
  }

  @Override
  public List<DonacionResponse> findDonacionesByIdEntidad(Long idEntidad) {
    EntidadBeneficiaria entidad = this.obtenerEntidad(idEntidad);
    List<Donacion> donaciones = new java.util.ArrayList<>(entidad.getDonacionesAsignadas());
    if (entidad.getNecesidades() != null) {
      for (Necesidad n : entidad.getNecesidades()) {
        if (n.getDonacionesAsignadas() != null) {
          for (Donacion d : n.getDonacionesAsignadas()) {
            if (!donaciones.contains(d)) {
              donaciones.add(d);
            }
          }
        }
      }
    }
    return donaciones.stream().map(this::mapearDonacionResponse).toList();
  }

  @Override
  public NecesidadDto findNecesidadById(Long idNecesidad) {
    return this.mapearNecesidad(
        necesidadRepository.findById(idNecesidad)
            .orElseThrow(()->new ResourceNotFoundException("No se encontró la necesidad con id " + idNecesidad.toString())));
  }

  @Override
  @Transactional
  public EntidadBeneficiariaResponse crear(EntidadBeneficiariaRequest request) {
    EntidadBeneficiaria entidad = new EntidadBeneficiaria();
    this.actualizarDatosDesdeRequest(entidad, request);
    this.entidadBeneficiariaRepository.save(entidad);
    return this.mapearEntidadResponse(entidad);
  }

    @Override
    @Transactional
    public EntidadBeneficiariaResponse actualizar(Long id, EntidadBeneficiariaRequest request) {
        EntidadBeneficiaria entidad = this.obtenerEntidad(id);
        this.actualizarDatosDesdeRequest(entidad, request);
        this.entidadBeneficiariaRepository.save(entidad);
        return this.mapearEntidadResponse(entidad);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        this.obtenerEntidad(id);
        this.entidadBeneficiariaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void agregarNecesidad(Long id, NecesidadRequest request) {
        EntidadBeneficiaria entidad = this.obtenerEntidad(id);

        Necesidad necesidad = new Necesidad();
        if(request.cantidad() <= 0){
          throw new BusinessException("La cantidad necesitada no puede ser negativa");
        }
        necesidad.setCantidad(request.cantidad());
        necesidad.setDescripcion(request.descripcion());
        necesidad.setFechaCreacion(LocalDate.now());
        necesidad.setEntidadBeneficiaria(entidad);

    Subcategoria sub = null;
    if (request.subcategoriaId() != null) {
      sub = this.subcategoriaService.buscarEntidadPorId(request.subcategoriaId());
    }

    if (sub != null) {
      necesidad.setSubcategoria(sub);
      necesidad.setUnidadDeMedida(sub.getUnidadDeMedida());
    }

    if (request.tipoNecesidad() != null) {
      necesidad.setTipoDeNecesidad(this.tiposNecesidad.get(request.tipoNecesidad().toUpperCase()));
    }


    if (request.tipoNecesidad().equals("RECURRENTE")) {
      necesidad.setPeriodoCorrespondiente(Period.parse(request.periodo()));
      necesidad.setInicioRecurrencia(request.inicioRecurrencia());
    }
    
    entidad.agregarNecesidad(necesidad);
    this.entidadBeneficiariaRepository.save(entidad);
  }

    @Override
    @Transactional
    public void eliminarNecesidad(Long id, Long idNecesidad) {
        EntidadBeneficiaria entidad = this.obtenerEntidad(id);
        boolean removed = entidad.getNecesidades().removeIf(n -> n.getId().equals(idNecesidad));
        if (!removed) {
            throw new ResourceNotFoundException("No se encontró la necesidad con ID: " + idNecesidad + " en la entidad: " + id);
        }
        this.entidadBeneficiariaRepository.save(entidad);
    }

    @Override
    public EntidadBeneficiaria findByRazonSocial(String razonSocial) {
        return this.entidadBeneficiariaRepository.findByRazonSocial(razonSocial)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la entidad beneficiaria con Razón Social: " + razonSocial));
    }

  private void actualizarDatosDesdeRequest(EntidadBeneficiaria entidadBeneficiaria, EntidadBeneficiariaRequest request) {
    entidadBeneficiaria.setRazonSocial(request.razonSocial());

    if (request.idUsuarioAuth() != null) {
      entidadBeneficiaria.setIdUsuarioAuth(request.idUsuarioAuth());
    }

    if (request.tipoEntidad() != null) {
      TipoEntidad tipoEntidad = new TipoEntidad();
      tipoEntidad.setTipoEntidad(request.tipoEntidad());
      entidadBeneficiaria.setTipo(tipoEntidad);
    }
    
    Direccion dir = new Direccion();
    dir.setCalle(request.calle());
    dir.setAltura(request.altura());
    entidadBeneficiaria.setDireccion(dir);

        if (request.contactos() != null) {
            entidadBeneficiaria.getContactos().clear();
            request.contactos().forEach(dto -> {
                entidadBeneficiaria.agregarContacto(new MedioDeContacto(dto.tipo(), dto.valor(), true));
            });
        }

        if (request.representantes() != null) {
            entidadBeneficiaria.getRepresentantes().clear();
            request.representantes().forEach(representanteDto -> {
                List<MedioDeContacto> medios = new ArrayList<>();
                if (representanteDto.contactos() != null && !representanteDto.contactos().isEmpty()) {
                    for (int i = 0; i < representanteDto.contactos().size(); i++) {
                        MedioDeContactoDTO c = representanteDto.contactos().get(i);
                        if (c != null && c.tipo() != null && c.valor() != null && !c.valor().isBlank()) {
                            boolean pref = c.esPreferido() != null ? c.esPreferido() : (i == 0);
                            medios.add(new MedioDeContacto(c.tipo(), c.valor().trim(), pref));
                        }
                    }
                } else if (representanteDto.contacto() != null && representanteDto.contacto().tipo() != null
                        && representanteDto.contacto().valor() != null && !representanteDto.contacto().valor().isBlank()) {
                    medios.add(new MedioDeContacto(representanteDto.contacto().tipo(), representanteDto.contacto().valor().trim(), true));
                }
                entidadBeneficiaria.agregarRepresentante(new Representante(representanteDto.nombre(), medios));
            });
        }
    }

    private EntidadBeneficiaria obtenerEntidad(Long id) {
        return this.entidadBeneficiariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la entidad beneficiaria con ID: " + id));
    }

    private EntidadBeneficiariaResponse mapearEntidadResponse(EntidadBeneficiaria entidad) {
        List<MedioDeContactoDTO> contactoDTOS = entidad.getContactos().stream()
                .map(contacto -> new MedioDeContactoDTO(contacto.getTipoDeContacto(), contacto.getValorDelContacto()))
                .collect(Collectors.toList());

        List<RepresentanteDTO> representanteDTOS = entidad.getRepresentantes().stream()
                .map(this::mapearRepresentante)
                .collect(Collectors.toList());

        List<NecesidadDto> necesidadesDtos = entidad.getNecesidades().stream()
                .map(this::mapearNecesidad)
                .collect(Collectors.toList());

        return new EntidadBeneficiariaResponse(
                entidad.getId(),
                entidad.getRazonSocial(),
                entidad.getTipo() != null ? entidad.getTipo().getTipoEntidad() : null,
                contactoDTOS,
                representanteDTOS,
                necesidadesDtos,
                entidad.getDonacionesAsignadas().stream().map(this::mapearDonacionResponse).toList(),
                mapearDireccion(entidad.getDireccion())
        );
    }

    private DireccionDto mapearDireccion(Direccion direccion) {
        if (direccion == null) {
            return null;
        }
        return new DireccionDto(
                direccion.getCalle(),
                direccion.getAltura(),
                direccion.getCiudad() != null ? direccion.getCiudad().getNombre() : null,
                direccion.getLatitud(),
                direccion.getLongitud()
        );
    }

  private NecesidadDto mapearNecesidad(Necesidad necesidad) {
    return new NecesidadDto(
        necesidad.getId(),
        necesidad.getSubcategoria() != null ? this.mapearSubcategoria( necesidad.getSubcategoria()) : null,
        necesidad.getTipoDeNecesidad() != null ? necesidad.getTipoDeNecesidad().obtenerNombre() : null,
        necesidad.getCantidad(),
        necesidad.getCantidadDonada(),
        necesidad.getPeriodoCorrespondiente(),
        necesidad.getInicioRecurrencia(),
        necesidad.getUltimoPeriodo().map(Periodo::getFechaFin).orElse(null),
        this.calcularTiempoRestante(necesidad),
        necesidad.getDonacionesAsignadas().stream().map(this::mapearDonacionResponse).toList(),
        this.mapearPeriodos(necesidad.getHistorialPeriodos())
    );
  }
  public static String calcularTiempoRestante(Necesidad necesidad) {
    Periodo periodo = necesidad.getUltimoPeriodo().orElse(null);
    if(periodo == null){
      return null;
    }
    String mensaje = "Reinicia";
    LocalDate fechaFutura = periodo.getFechaFin();

    if(periodo.getFechaInicio().equals(necesidad.getInicioRecurrencia())
    && !periodo.getFechaInicio().equals(LocalDate.now())){
      fechaFutura = periodo.getFechaInicio();
      mensaje = "Empieza";
    }

    if(fechaFutura == null){
      return null;
    }
    // Obtenemos el momento actual (fecha y hora)
    LocalDateTime ahora = LocalDateTime.now();

    // Convertimos el LocalDate a LocalDateTime asumiendo que es a las 00:00 de ese día
    LocalDateTime fechaFuturaConHora = fechaFutura.atStartOfDay();

    Duration duracion = Duration.between(ahora, fechaFuturaConHora);

    // Validamos que la fecha no haya pasado ya
    if (duracion.isNegative() || duracion.isZero()) {
      return "Período en renovación";
    }

    long dias = duracion.toDays();
    long horas = duracion.toHours();
    long minutos = duracion.toMinutes();

    if (dias >= 1) {
      return mensaje + " en " + (dias + (horas > 0 ? 1 : 0)) + (dias == 1 ? " día" : " días");
    } else if (horas >= 1) {
      return mensaje + " en " + horas + (horas == 1 ? " hora" : " horas");
    } else if (minutos >= 1) {
      return mensaje + " en " + minutos + (minutos == 1 ? " minuto" : " minutos");
    } else {
      return mensaje + " en menos de un minuto";
    }
  }
  private SubcategoriaResponse mapearSubcategoria(Subcategoria subcategoria){
    return new SubcategoriaResponse(
        subcategoria.getId(),
        subcategoria.getNombre(),
        subcategoria.getCategoria().getId(),
        subcategoria.getCategoria().getNombre(),
        subcategoria.getUnidadDeMedida()
    );
  }
  private List<PeriodoDto> mapearPeriodos(List<Periodo> periodos){
    return periodos.stream().map(periodo ->
        new PeriodoDto(periodo.getId(),
            periodo.getFechaInicio(),
            periodo.getFechaFin(),
            periodo.getCantidadDonada())).toList();
  }
  private RepresentanteDTO mapearRepresentante(Representante representante) {
    List<MedioDeContactoDTO> contactosDto = representante.getContactos() == null ? List.of() : representante.getContactos().stream()
        .filter(Objects::nonNull)
        .map(c -> new MedioDeContactoDTO(c.getTipoDeContacto(), c.getValorDelContacto(), c.getEsPreferido()))
        .toList();
    return new RepresentanteDTO(representante.getId_representante(), representante.getNombre(), contactosDto);
  }

    @Override
    public List<Necesidad> findAllNecesidades() {
        return this.necesidadRepository.findAll();
    }

    @Override
    public Necesidad findByNecesidadId(Long idNecesidad) {
        return this.necesidadRepository.findById(idNecesidad).orElseThrow(() -> new ResourceNotFoundException("No se encontró la necesidad con ID: " + idNecesidad));
    }

  @Override
  @Transactional
  public void guardarEntidad(EntidadBeneficiaria entidadBeneficiaria){
    this.entidadBeneficiariaRepository.save(entidadBeneficiaria);
  }
  public DonacionResponse mapearDonacionResponse(Donacion donacion){
    List<BienResponse> bienesResponse = this.mapearBienes(donacion.getBienes());

    EstadoDeUnaDonacion ultimoEstado = donacion.getEstadosDeDonaciones().getLast();
    TipoDeEstado estadoResponse = ultimoEstado.getTipo();
    LocalDate fechaUltimaActualizacion = ultimoEstado.getFecha();

    return new DonacionResponse(
        donacion.getId(),
        bienesResponse,
        bienesResponse.stream().mapToDouble(BienResponse::cantidad).sum(),
        donacion.getDescripcionGeneral(),
        estadoResponse,
        fechaUltimaActualizacion,
        this.mapearSubcategoria( donacion.getSubcategoria()),
        donacion.getNecesidad() != null ? donacion.getNecesidad().getId() : null,
        donacion.getPeriodo() != null ? donacion.getPeriodo().getId() : null,
        donacion.getNecesidad() != null ? donacion.getNecesidad().getEntidadBeneficiaria().getRazonSocial():null
    );
  }
  private List<BienResponse> mapearBienes(List<Bien> bienes) {
    return bienes.stream().map(bien -> new BienResponse(bien.getNombre(), bien.getCantidad(), bien.getDescripcion())).collect(Collectors.toList());
  }

}

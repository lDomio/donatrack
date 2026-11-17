package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.BusinessException;
import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.AdministradorRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaSimpleResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.*;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadDto;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.PeriodoDto;
import ar.edu.utn.frba.dds.donaciones.main.dtos.notificacion.MedioDeContactoDto;
import ar.edu.utn.frba.dds.donaciones.main.dtos.sugerenciaMatch.SugerenciaMatchResponse;
import ar.edu.utn.frba.dds.donaciones.main.events.dtos.InicioRutaEventDtoSalida;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.IncentivosClient;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.LogisticaClient;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.NotificacionClient;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.impl.CompatibilidadSemantica;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.impl.PrioridadSubAtendidos;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.*;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Humana;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Juridica;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.EstadoDeUnaDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.matcheadorDonaciones.MatcheadorDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.matcheadorDonaciones.MatcheoSugerido;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Periodo;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.repositories.SugerenciaMatchRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.DonacionService;
import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import ar.edu.utn.frba.dds.donaciones.main.services.EntidadBeneficiariaService;
import ar.edu.utn.frba.dds.donaciones.main.services.SubcategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class DonacionServiceImpl implements DonacionService {
    private final DonacionRepository donacionRepository;
    private final DonanteService donanteService;
    private final EntidadBeneficiariaService entidadBeneficiariaService;
    private final SubcategoriaService subcategoriaService;
    private final SugerenciaMatchRepository sugerenciaMatchRepository;
    private final IncentivosClient incentivosClient;
    private final NotificacionClient notificacionClient;
    private final LogisticaClient logisticaClient;
    private final Clock clock;

    public DonacionServiceImpl(DonacionRepository donacionRepository, DonanteService donanteService, EntidadBeneficiariaService entidadBeneficiariaService, SubcategoriaService subcategoriaService, SugerenciaMatchRepository sugerenciaMatchRepository, IncentivosClient incentivosClient, NotificacionClient notificacionClient, LogisticaClient logisticaClient, Clock clock) {
        this.donacionRepository = donacionRepository;
        this.donanteService = donanteService;
        this.entidadBeneficiariaService = entidadBeneficiariaService;
        this.subcategoriaService = subcategoriaService;
        this.sugerenciaMatchRepository = sugerenciaMatchRepository;
        this.incentivosClient = incentivosClient;
        this.notificacionClient = notificacionClient;
        this.logisticaClient = logisticaClient;
        this.clock = clock;
    }

    @Override
    public List<DonacionResponse> obtenerTodas() {
        return donacionRepository.findAll().stream().map(this::mapearDonacionResponse).collect(Collectors.toList());
    }

    @Override
    public List<DonacionResponse> obtenerPorIdNecesidad(Long idNecesidad) {
        return donacionRepository.findAll().stream()
                .filter(d -> d.getNecesidad() != null && idNecesidad.equals(d.getNecesidad().getId()))
                .map(this::mapearDonacionResponse).collect(Collectors.toList());
    }

    public DonacionResponse mapearDonacionResponse(Donacion donacion) {
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
                mapearSubcategoria(donacion.getSubcategoria()),
                donacion.getNecesidad() != null ? donacion.getNecesidad().getId() : null,
                donacion.getPeriodo() != null ? donacion.getPeriodo().getId() : null,
                donacion.getFotoEntrega(),
                donacion.getNecesidad() != null? donacion.getNecesidad().getEntidadBeneficiaria().getRazonSocial(): null
        );
    }

    private List<BienResponse> mapearBienes(List<Bien> bienes) {
        return bienes.stream().map(bien -> new BienResponse(bien.getNombre(), bien.getCantidad(), bien.getDescripcion())).collect(Collectors.toList());
    }

    private SubcategoriaResponse mapearSubcategoria(Subcategoria subcategoria) {
        if (subcategoria == null) {
            return null;
        }

        return new SubcategoriaResponse(
                subcategoria.getId(),
                subcategoria.getNombre(),
                subcategoria.getCategoria() != null ? subcategoria.getCategoria().getId() : null,
                subcategoria.getCategoria() != null ? subcategoria.getCategoria().getNombre() : null,
                subcategoria.getUnidadDeMedida()
        );
    }

    @Override
    public List<DonacionResponse> obtenerDonaciones(Long id) {
        //Validamos que el donante exista antes de buscar sus donaciones
        this.donanteService.obtenerDonante(id);
        return donacionRepository.findByIdDonante(id).stream().map(this::mapearDonacionResponse).collect(Collectors.toList());
    }

    @Override
    public EntidadesAyudadasResponse contarEntidadesAyudadasEsteMes(Long idDonante) {
        //404 si el donante no existe, igual que obtenerDonaciones
        this.donanteService.obtenerDonante(idDonante);
        YearMonth mesActual = YearMonth.now(clock);
        LocalDate inicio = mesActual.atDay(1);
        LocalDate fin = mesActual.atEndOfMonth();
        long cantidad = donacionRepository.contarEntidadesDistintasAyudadas(idDonante, inicio, fin);
        return new EntidadesAyudadasResponse(idDonante, mesActual.getYear(), mesActual.getMonthValue(), cantidad);
    }


    @Override
    @Transactional
    public List<DonacionResponse> crearDonacion(CrearDonacionRequest request) {

        this.donanteService.obtenerDonante(request.idDonante());

        List<Bien> bienes = request.bienes().stream()
                .map(this::mapearBien)
                .collect(Collectors.toList());

        SegmentadorDeFormularioDonacion segmentador = new SegmentadorDeFormularioDonacion();
        List<Donacion> donaciones = segmentador.segmentar(bienes);

        donaciones.forEach(donacion -> {
            donacion.setIdDonante(request.idDonante());
            donacion.setDescripcionGeneral(request.descripcionGeneral());
            Donacion guardada = this.donacionRepository.save(donacion);

            String subcategoriaNombre = donacion.getSubcategoria() != null ? donacion.getSubcategoria().getNombre() : "Sin subcategoría";
            String categoriaNombre = (donacion.getSubcategoria() != null && donacion.getSubcategoria().getCategoria() != null)
                    ? donacion.getSubcategoria().getCategoria().getNombre()
                    : "Sin categoría";

            try {
                this.incentivosClient.agregarDonacion(
                        guardada.getId(),
                        request.idDonante(),
                        donacion.getCantidad(),
                        subcategoriaNombre,
                        categoriaNombre,
                        donacion.getFechaDeCreacion(),
                        null
                );
            } catch (Exception e) {
                Logger.getLogger(DonacionServiceImpl.class.getName()).warning("Error al notificar incentivos al crear donación: " + e.getMessage());
            }
        });

        return donaciones.stream()
                .map(this::mapearDonacionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonacionResponse actualizarEstado(Long id, CambiarEstadoRequest request) {
        Donacion donacion = donacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la donación con ID: " + id));

        TipoDeEstado nuevoTipo = TipoDeEstado.valueOf(request.nuevoEstado().toUpperCase());

        if (!donacion.puedeCambiarDeEstado(nuevoTipo)) {
            throw new BusinessException("Transición inválida: de " + donacion.getUltimoEstado() + " a " + nuevoTipo);
        }

        EstadoDeUnaDonacion nuevoEstado = new EstadoDeUnaDonacion(
                nuevoTipo,
                request.fechaNuevoEstado() != null ? request.fechaNuevoEstado() : LocalDate.now(),
                request.motivo(),
                request.autorDelCambio()
        );

        donacion.agregarEstado(nuevoEstado);

        if (request.fotoEntrega() != null && !request.fotoEntrega().isBlank()) {
            donacion.setFotoEntrega(request.fotoEntrega());
        }

        if (nuevoTipo == TipoDeEstado.ENTREGA_FALLIDA || nuevoTipo == TipoDeEstado.DEPOSITO) {
            if (donacion.getNecesidad() != null) {
                EntidadBeneficiaria entidad = donacion.getNecesidad().getEntidadBeneficiaria();
                donacion.desasignar();
                if (entidad != null) {
                    this.entidadBeneficiariaService.guardarEntidad(entidad);
                }
            }
        }

        this.donacionRepository.save(donacion);

        if (nuevoTipo == TipoDeEstado.DEPOSITO) {
            this.realizarMatcheoDonaciones();
        }

        return this.mapearDonacionResponse(donacion);
    }

    private Bien mapearBien(BienRequest request) {
        LocalDate hoy = LocalDate.now(clock);
        if (request.fechaVencimiento() != null && request.fechaVencimiento().isBefore(hoy)) {
            throw new BusinessException("El bien '" + request.nombre() + "' tiene fecha de vencimiento "
                    + request.fechaVencimiento() + ", anterior a la fecha actual (" + hoy
                    + "). No se pueden registrar bienes vencidos.");
        }

        Bien bien = new Bien();
        if(request.nombre() == null){
            throw new BusinessException("El bien tiene que tener nombre");
        }
        if(request.cantidad() != null){
            if(request.cantidad() <=0){
                throw new BusinessException("El bien tiene que tener cantidad");
            }
        }
        else{
            throw new BusinessException("El bien tiene que tener cantidad");
        }
        if(request.subcategoriaId() ==null){
            throw new BusinessException("El bien tiene que tener el id de la subcategoria");
        }
        if(request.estado() != null){
            if(!request.estado().equals(EstadoDeBienNoPerecedero.USADO.toString()) && !request.estado().equals(EstadoDeBienNoPerecedero.NUEVO.toString())){
                throw new BusinessException("El bien tiene que tener el estado Nuevo o Usado");
            }
        }
        else{
            throw new BusinessException("El bien tiene que tener estado Nuevo o Usado");
        }
        if(request.fechaVencimiento() != null && request.fechaVencimiento().isBefore(LocalDate.now())){
            throw new BusinessException("El bien tiene que tener fecha de vencimiento en el futuro");
        }
        bien.setNombre(request.nombre());
        bien.setDescripcion(request.descripcion());
        bien.setCantidad(request.cantidad());

        // Subcategoría y Unidad
        if (request.subcategoriaId() != null) {
            Subcategoria sub = this.subcategoriaService.buscarEntidadPorId(request.subcategoriaId());
            bien.setSubcategoria(sub);
            bien.setUnidadDeMedida(sub.getUnidadDeMedida());
        } else if (request.unidadDeMedida() != null) {
            bien.setUnidadDeMedida(request.unidadDeMedida());
        }

        //Foto
        if (request.urlFoto() != null) {
            bien.setFoto(new Foto(request.urlFoto(), "Foto de " + request.nombre()));
        }

        // Estado
        if (request.estado() != null) {
            if (request.estado().equalsIgnoreCase("NUEVO")) {
                bien.setEstado(EstadoDeBienNoPerecedero.NUEVO);
            } else {
                bien.setEstado(EstadoDeBienNoPerecedero.USADO);
            }
        }

        // Vencimiento
        if (request.fechaVencimiento() != null) {
            bien.setFechaVencimiento(request.fechaVencimiento());
        }

        return bien;
    }

    @Override
    @Transactional
    public void eliminarDonacion(Long id) {
        this.donacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la donación con ID: " + id));
        this.donacionRepository.deleteById(id);
    }

    @Override
    public List<SugerenciaMatchResponse> sugerenciaDeMatcheoDeDonacion() {
        return this.sugerenciaMatchRepository.findAll().stream()
                .map(this::mapearSugerenciaResponse)
                .collect(Collectors.toList());
    }

    private SugerenciaMatchResponse mapearSugerenciaResponse(MatcheoSugerido sugerencia) {
        return new SugerenciaMatchResponse(
                sugerencia.getId(),
                sugerencia.getDonacion().getId(),
                this.mapeoNecesidades(sugerencia.getDonacion(), sugerencia.getNecesidadesSugeridas()),
                sugerencia.getYaFueSeleccionadoPorElAdministrador()
        );
    }

    // ----------------- MAPEO NECESIDADES (Rama feature) -----------------
    private List<NecesidadDto> mapeoNecesidades(Donacion donacion, List<Necesidad> necesidadesSugeridas) {
        List<Necesidad> ordenSemantica = donacion != null
                ? new CompatibilidadSemantica().aplicarCriterioDeSelecion(donacion, necesidadesSugeridas)
                : necesidadesSugeridas;
        List<Necesidad> ordenPrioridad = donacion != null
                ? new PrioridadSubAtendidos().aplicarCriterioDeSelecion(donacion, necesidadesSugeridas)
                : necesidadesSugeridas;

        return necesidadesSugeridas.stream().map(necesidad -> {
            int rankSem = Math.max(1, ordenSemantica.indexOf(necesidad) + 1);
            int rankPrio = Math.max(1, ordenPrioridad.indexOf(necesidad) + 1);
            int cantDonacionesTrimestre = necesidad.getDonacionesUltimoTrimestreDeEntidad() != null
                    ? necesidad.getDonacionesUltimoTrimestreDeEntidad().size()
                    : 0;
            double scoreSem = Math.round((1.0 / rankSem) * 100.0) / 100.0;
            double scorePrio = Math.round((1.0 / (1.0 + 0.1 * cantDonacionesTrimestre)) * 100.0) / 100.0;

            String etiquetaAlgoritmo;
            if (rankSem == 1 && rankPrio == 1) {
                etiquetaAlgoritmo = "Consenso (Ambos Algoritmos)";
            } else if (rankSem < rankPrio) {
                etiquetaAlgoritmo = "Compatibilidad Semántica";
            } else if (rankPrio < rankSem) {
                etiquetaAlgoritmo = "Prioridad Sub-atendidos";
            } else {
                etiquetaAlgoritmo = "Ambos Algoritmos";
            }

            String razonSocial = necesidad.getEntidadBeneficiaria() != null
                    ? necesidad.getEntidadBeneficiaria().getRazonSocial()
                    : "Entidad Beneficiaria";

            return new NecesidadDto(
                    necesidad.getId(),
                    necesidad.getSubcategoria() != null ? this.mapearSubcategoria(necesidad.getSubcategoria()) : null,
                    necesidad.getTipoDeNecesidad() != null ? necesidad.getTipoDeNecesidad().obtenerNombre() : null,
                    necesidad.getCantidad(),
                    necesidad.getCantidadDonada(),
                    necesidad.getPeriodoCorrespondiente(),
                    necesidad.getInicioRecurrencia(),
                    necesidad.getUltimoPeriodo().map(Periodo::getFechaFin).orElse(null),
                    EntidadBeneficiariaImpl.calcularTiempoRestante(necesidad),
                    necesidad.getDonacionesAsignadas().stream().map(this::mapearDonacionResponse).toList(),
                    this.mapearPeriodos(necesidad.getHistorialPeriodos()),
                    razonSocial,
                    necesidad.getDescripcion(),
                    etiquetaAlgoritmo,
                    rankSem,
                    scoreSem,
                    rankPrio,
                    scorePrio,
                    cantDonacionesTrimestre
            );
        }).collect(Collectors.toList());
    }
    // ---------------------------------------------------------------------

    private List<PeriodoDto> mapearPeriodos(List<Periodo> periodos) {
        return periodos.stream().map(periodo ->
                new PeriodoDto(periodo.getId(),
                        periodo.getFechaInicio(),
                        periodo.getFechaFin(),
                        periodo.getCantidadDonada())).toList();
    }

    @Transactional
    public void realizarMatcheoDonaciones() {
        List<Necesidad> todasLasNecesidades = entidadBeneficiariaService.findAllNecesidades();
        List<MatcheoSugerido> existentes = sugerenciaMatchRepository.findAll();

        MatcheadorDonacion matcheador = new MatcheadorDonacion(List.of(
                new CompatibilidadSemantica(),
                new PrioridadSubAtendidos()
        ));
        this.donacionRepository.findAll().stream()
                .filter(donacion -> donacion.getEstadosDeDonaciones() != null
                        && !donacion.getEstadosDeDonaciones().isEmpty()
                        && donacion.getEstadosDeDonaciones().getLast().getTipo() == TipoDeEstado.DEPOSITO)
                .forEach(donacion -> {
                    MatcheoSugerido nuevo = matcheador.seleccionarNecesidad(donacion, todasLasNecesidades);
                    MatcheoSugerido existentePendiente = existentes.stream()
                            .filter(m -> m.getDonacion() != null
                                    && m.getDonacion().getId() != null
                                    && m.getDonacion().getId().equals(donacion.getId())
                                    && !Boolean.TRUE.equals(m.getYaFueSeleccionadoPorElAdministrador()))
                            .findFirst()
                            .orElse(null);

                    if (existentePendiente != null) {
                        existentePendiente.setNecesidadesSugeridas(new ArrayList<>(nuevo.getNecesidadesSugeridas()));
                        sugerenciaMatchRepository.save(existentePendiente);
                    } else {
                        sugerenciaMatchRepository.save(nuevo);
                    }
                });
    }

    @Override
    @Transactional
    public void seleccionarNecesidad(Long idMatcheo, Long idNecesidadRecibidora, AdministradorRequest request) {
        Necesidad necesidad = entidadBeneficiariaService.findByNecesidadId(idNecesidadRecibidora);
        MatcheoSugerido matcheoSugerido = sugerenciaMatchRepository.findById(idMatcheo).orElseThrow(() -> new ResourceNotFoundException("No se encontró el matcheo con ID: " + idMatcheo));
        Donacion donacion = donacionRepository.findById(matcheoSugerido.getDonacion().getId()).orElseThrow(() -> new ResourceNotFoundException("No se encontró la donación con ID: " + matcheoSugerido.getDonacion().getId()));


        matcheoSugerido.setYaFueSeleccionadoPorElAdministrador(true);
        sugerenciaMatchRepository.save(matcheoSugerido);


        if (!donacion.puedeCambiarDeEstado(TipoDeEstado.ASIGNACION_REALIZADA)) {
            throw new BusinessException("Transición inválida: de " + donacion.getUltimoEstado() + " a " + TipoDeEstado.ASIGNACION_REALIZADA);
        }

        donacion.agregarEstado(new EstadoDeUnaDonacion(TipoDeEstado.ASIGNACION_REALIZADA, LocalDate.now(), "Asignacion de Donacion", request.autorDelCambio()));
        donacion.asignarNecesidad(necesidad);

        this.entidadBeneficiariaService.guardarEntidad(necesidad.getEntidadBeneficiaria());

        donacionRepository.save(donacion);

        try {
            incentivosClient.actualizarDonacion(donacion.getId(), necesidad.getEntidadBeneficiaria().getRazonSocial());
        } catch (Exception e) {
            Logger.getLogger(DonacionServiceImpl.class.getName()).warning("Error al actualizar incentivos: " + e.getMessage());
        }

        try {
            logisticaClient.donacionAsignada(
                    donacion.getId(),
                    donacion.getIdDonante(),
                    donacion.getSubcategoria() != null ? donacion.getSubcategoria().getNombre() : "Sin subcategoría",
                    donacion.getCantidad(),
                    donacion.fechaMasProximaDeVencimiento(),
                    necesidad.getEntidadBeneficiaria().getRazonSocial(),
                    necesidad.getEntidadBeneficiaria().getId(),
                    necesidad.getEntidadBeneficiaria().getDireccion() != null ? necesidad.getEntidadBeneficiaria().getDireccion().getCalle() : "",
                    necesidad.getEntidadBeneficiaria().getDireccion() != null ? necesidad.getEntidadBeneficiaria().getDireccion().getLatitud() : null,
                    necesidad.getEntidadBeneficiaria().getDireccion() != null ? necesidad.getEntidadBeneficiaria().getDireccion().getLongitud() : null
            );
        } catch (Exception e) {
            Logger.getLogger(DonacionServiceImpl.class.getName()).warning("Error al notificar a logística: " + e.getMessage());
        }

        // Notificaciones de asignación (Casos 2 y 3)
        try {
            // Mapear contactos de la entidad
            List<MedioDeContactoDto> contactosEntidad = new ArrayList<>();
            if (necesidad.getEntidadBeneficiaria().getContactos() != null) {
                contactosEntidad = necesidad.getEntidadBeneficiaria().getContactos().stream()
                        .map(contacto -> new MedioDeContactoDto(contacto.getValorDelContacto(), contacto.getTipoDeContacto().name()))
                        .collect(Collectors.toList());
            }

            // Notificar a la entidad beneficiaria
            if (!contactosEntidad.isEmpty()) {
                notificacionClient.notificarAsignacionEntidad(necesidad.getEntidadBeneficiaria().getRazonSocial(), contactosEntidad, donacion.getDescripcionGeneral());
            }

            // Notificar al donante
            Donante donante = donanteService.obtenerDonante(donacion.getIdDonante());
            if (donante != null) {
                String nombreDonante = obtenerNombreDonante(donante);
                if (donante.getContactos() != null) {
                    List<MedioDeContactoDto> contactosDonante = donante.getContactos().stream()
                            .map(contacto -> new MedioDeContactoDto(contacto.getValorDelContacto(), contacto.getTipoDeContacto().name()))
                            .collect(Collectors.toList());

                    if (!contactosDonante.isEmpty()) {
                        notificacionClient.notificarAsignacionDonante(nombreDonante, contactosDonante, donacion.getDescripcionGeneral(), necesidad.getEntidadBeneficiaria().getRazonSocial());
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(DonacionServiceImpl.class.getName()).warning("Error al enviar notificaciones de asignación: " + e.getMessage());
        }
    }

    @Override
    public void notificarDonanteEntidadBeneficiaria(List<InicioRutaEventDtoSalida> eventoInicioDeRuta) {
        for (InicioRutaEventDtoSalida dto : eventoInicioDeRuta) {
            MedioDeContacto medio = dto.medioDeContacto();
            notificacionClient.enviarNotificacionGeneral(
                    "",
                    List.of(new MedioDeContactoDto(medio.getValorDelContacto(), medio.getTipoDeContacto().name())),
                    dto.mensaje());
        }
    }

    private String obtenerNombreDonante(Donante donante) {
        if (donante instanceof Humana h) {
            return h.getNombre() + " " + h.getApellido();
        } else if (donante instanceof Juridica j) {
            return j.getRazonSocial();
        }
        return "Donante";
    }

    @Override
    public MedioDeContacto recuperarMedioContactoDonante(Long id) {
        Donante donante = donanteService.obtenerDonante(id);
        return donante.getContactos().stream().filter(MedioDeContacto::getEsPreferido).toList().getFirst();
    }

    @Override
    public MedioDeContacto recuperarMedioContactoEntidadBeneficiaria(String razonSocial) {
        EntidadBeneficiaria entidadBeneficiaria = entidadBeneficiariaService.findByRazonSocial(razonSocial);
        return entidadBeneficiaria.getContactos().stream().filter(MedioDeContacto::getEsPreferido).toList().getFirst();
    }

    @Override
    public List<CasoExitoResponse> obtenerCasosExito() {
        return donacionRepository.findAll().stream()
                .filter(d -> d.getEstadosDeDonaciones() != null
                        && !d.getEstadosDeDonaciones().isEmpty()
                        && d.getUltimoEstado() == TipoDeEstado.ENTREGADA)
                .map(d -> {
                    EntidadBeneficiaria entidad = d.getNecesidad() != null ? d.getNecesidad().getEntidadBeneficiaria() : null;
                    String nombreEntidad = entidad != null ? entidad.getRazonSocial() : "Comedor Comunitario";
                    String direccion = (entidad != null && entidad.getDireccion() != null)
                            ? entidad.getDireccion().getCalle() : "CABA";
                    Double lat = (entidad != null && entidad.getDireccion() != null && entidad.getDireccion().getLatitud() != null)
                            ? entidad.getDireccion().getLatitud() : -34.6037;
                    Double lng = (entidad != null && entidad.getDireccion() != null && entidad.getDireccion().getLongitud() != null)
                            ? entidad.getDireccion().getLongitud() : -58.3816;
                    String subNombre = d.getSubcategoria() != null ? d.getSubcategoria().getNombre() : "Donación";
                    String unidad = d.getSubcategoria() != null ? d.getSubcategoria().getUnidadDeMedida() : "unid";
                    String titulo = subNombre + " x" + d.getCantidad().intValue() + " " + unidad;
                    return new CasoExitoResponse(
                            d.getId(),
                            titulo,
                            d.getDescripcionGeneral(),
                            nombreEntidad,
                            direccion,
                            lat,
                            lng,
                            d.getFotoEntrega(),
                            d.fechaUltimoEstado()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public DonacionResponse obtenerDonacionPorId(Long idDonacion) {
        return mapearDonacionResponse(this.donacionRepository.findById(idDonacion).orElseThrow(()->new ResourceNotFoundException("No se encontró la donación")));
    }
}

package ar.edu.utn.frba.dds.logistica.main.services.impl;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.ActualizarEstadoEntregaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.EntregaResponse;
import ar.edu.utn.frba.dds.logistica.main.events.EntregaEventPublisher;
import ar.edu.utn.frba.dds.logistica.main.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.DonacionesClient;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.IncentivosClient;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.DonacionEnEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.TipoDeEstadoDonacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.EstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.TipoDeEstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntidadBeneficiariaRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntregaRepository;
import ar.edu.utn.frba.dds.logistica.main.services.EntregaService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDate;

@Service
public class EntregaServiceImpl implements EntregaService {
    private final EntregaRepository repository;
    private final DonacionesClient donacionesClient;
    private final IncentivosClient incentivosClient;
    private final DonacionRepository donacionRepository;
    private final EntidadBeneficiariaRepository entidadRepository;
    private final EntregaEventPublisher entregaEventPublisher;

    public EntregaServiceImpl(EntregaRepository repository,
            DonacionesClient donacionesClient,
            IncentivosClient incentivosClient,
            DonacionRepository donacionRepository,
            EntidadBeneficiariaRepository entidadRepository,
            EntregaEventPublisher entregaEventPublisher) {
        this.repository = repository;
        this.donacionesClient = donacionesClient;
        this.incentivosClient = incentivosClient;
        this.donacionRepository = donacionRepository;
        this.entidadRepository = entidadRepository;
        this.entregaEventPublisher = entregaEventPublisher;
    }

    @Override
    public List<EntregaResponse> listarPendientes() {
        return repository.findPendientes().stream().map(this::mapearEntrega).collect(Collectors.toList());
    }

    @Override
    public EntregaResponse buscarPorId(Long id) {
        return repository.findById(id).map(this::mapearEntrega)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));
    }

    @Override
    public EntregaResponse actualizarEstado(Long id, ActualizarEstadoEntregaRequest request) {
        Entrega entrega = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));

        TipoDeEstadoEntrega nuevoTipo = TipoDeEstadoEntrega.valueOf(request.nuevoEstado().toUpperCase());
        LocalDate fecha = request.fecha() != null ? request.fecha() : LocalDate.now();

        EstadoEntrega nuevoEstado = new EstadoEntrega(request.observaciones(), fecha, nuevoTipo);
        entrega.agregarEstado(nuevoEstado);

        procesarAccionesPorCambioDeEstado(entrega, nuevoTipo, request);

        repository.guardar(entrega);
        return mapearEntrega(entrega);
    }

    private void procesarAccionesPorCambioDeEstado(Entrega entrega, TipoDeEstadoEntrega nuevoTipo,
            ActualizarEstadoEntregaRequest request) {
        if (nuevoTipo == TipoDeEstadoEntrega.EXITOSA) {
            entrega.setFueRecibidoPorLaEntidad(true);
            entregaEventPublisher.publicarEntregaExitosa(entrega);
            entrega.getDonacionesAEntregar().forEach(donacionEnEntrega -> {
                donacionesClient.actualizarEstado(donacionEnEntrega.getId(), "ENTREGADA",
                        "Entrega exitosa en logística", "Sistema Logistica");

                donacionRepository.findById(donacionEnEntrega.getId()).ifPresent(donacion -> {
                    donacion.cambiarEstado(TipoDeEstadoDonacion.ENTREGADA);
                    donacionRepository.guardar(donacion);

                    String nombreEntidad = "";
                    if (donacion.getEntidadId() != null) {
                        nombreEntidad = entidadRepository.findById(donacion.getEntidadId())
                                .map(EntidadBeneficiaria::getRazonSocial)
                                .orElse("Entidad Desconocida");
                    }
                    incentivosClient.donacionRecibida(donacion.getId(), nombreEntidad);
                });
            });
        } else if (nuevoTipo == TipoDeEstadoEntrega.NO_RECIBIDA) {
            entregaEventPublisher.publicarEntregaNoSatisfactoria(entrega, request.observaciones());
            // notificar a Donaciones que hubo un fallo
            entrega.getDonacionesAEntregar().forEach(donacion -> {
                donacionesClient.actualizarEstado(donacion.getId(), "ENTREGA_FALLIDA", request.observaciones(),
                        "Sistema Logistica");

                // LIMPIEZA LOCAL EN LOGISTICA
                donacionRepository.findById(donacion.getId()).ifPresent(dLocal -> {
                    // quitar de la lista de la entidad en logistica
                    if (dLocal.getEntidadId() != null) {
                        entidadRepository.findById(dLocal.getEntidadId()).ifPresent(entidad -> {
                            entidad.removerDonacionAsignada(dLocal.getId());
                            entidadRepository.guardar(entidad);
                        });
                    }
                    // Eliminar la donación local para que quede lista para una futura re-asignación desde cero
                    donacionRepository.eliminar(dLocal.getId());
                });
            });
        } else if (nuevoTipo == TipoDeEstadoEntrega.EN_DEPOSITO) {
            // La donación permanece en ENTREGA_FALLIDA en donaciones-service hasta que el Administrador la reingrese manualmente al depósito
            entrega.getDonacionesAEntregar().forEach(donacion -> {
                // LIMPIEZA LOCAL EN LOGISTICA
                donacionRepository.findById(donacion.getId()).ifPresent(dLocal -> {
                    // eliminar la donación de mi servicio para evitar duplicados en futuras
                    // asignaciones
                    donacionRepository.eliminar(dLocal.getId());
                });
            });
        } else if (nuevoTipo == TipoDeEstadoEntrega.EN_CAMINO) {
            entrega.getDonacionesAEntregar().forEach(donacion -> {
                donacionesClient.actualizarEstado(donacion.getId(), "EN_TRASLADO", request.observaciones(),
                        "Sistema Logistica");

            });
        } else if (nuevoTipo == TipoDeEstadoEntrega.PENDIENTE) {
            entrega.getDonacionesAEntregar().forEach(donacion -> {
                donacionesClient.actualizarEstado(donacion.getId(), "LISTA_PARA_ENTREGAR", request.observaciones(),
                        "Sistema Logistica");
            });
        }
    }

    private EntregaResponse mapearEntrega(Entrega entrega) {
        String ultimoEstado = entrega.getEstadosEntrega().isEmpty() ? "SIN_ESTADO"
                : entrega.getEstadosEntrega().getLast().getTipoEstadoEntrega().toString();

        List<Long> donacionesIds = entrega.getDonacionesAEntregar().stream()
                .map(DonacionEnEntrega::getId).collect(Collectors.toList());

        return new EntregaResponse(
                entrega.getId(),
                entrega.getDireccionEntidad(),
                entrega.getLatitud(),
                entrega.getLongitud(),
                entrega.getFueRecibidoPorLaEntidad(),
                ultimoEstado,
                donacionesIds);
    }
}

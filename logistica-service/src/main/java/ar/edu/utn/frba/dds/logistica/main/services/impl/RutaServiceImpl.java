package ar.edu.utn.frba.dds.logistica.main.services.impl;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.ActualizarEstadoEntregaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.EntregaResponse;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.ActualizarEstadoRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.ParadaResponse;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.RutaResponse;
import ar.edu.utn.frba.dds.logistica.main.events.InicioRutaEventPublisher;
import ar.edu.utn.frba.dds.logistica.main.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.DonacionesClient;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.DonacionEnEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.TipoDeEstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.EstadoDeLaRuta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Parada;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.TipoDeEstadoRuta;
import ar.edu.utn.frba.dds.logistica.main.repositories.CamionRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntregaRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.RutaRepository;
import ar.edu.utn.frba.dds.logistica.main.services.EntregaService;
import ar.edu.utn.frba.dds.logistica.main.services.RutaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RutaServiceImpl implements RutaService {
    private final RutaRepository repository;
    private final EntregaService entregaService;
    private final InicioRutaEventPublisher inicioRutaEventPublisher;

    public RutaServiceImpl(RutaRepository repository,
            EntregaService entregaService, InicioRutaEventPublisher inicioRutaEventPublisher) {
        this.repository = repository;
        this.entregaService = entregaService;
        this.inicioRutaEventPublisher = inicioRutaEventPublisher;
    }

    @Override
    public List<RutaResponse> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::mapearRutaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RutaResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::mapearRutaResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada con ID: " + id));
    }

    @Override
    public void procesarResultadoRouting(RoutingResult request) {
        return;
    }



    @Override
    public RutaResponse actualizarEstado(Long id, ActualizarEstadoRutaRequest request) {
        // En un futuro -> validar que el chofer exista
        Ruta ruta = repository.findById(id).orElseGet(() -> {
            Ruta nueva = new Ruta();
            nueva.setId(id);
            repository.guardar(nueva);
            return nueva;
        });

        TipoDeEstadoRuta nuevoTipo = TipoDeEstadoRuta.valueOf(request.nuevoEstado().toUpperCase());
        EstadoDeLaRuta nuevoEstado = new EstadoDeLaRuta(LocalDate.now(), nuevoTipo);
        ruta.agregarEstado(nuevoEstado);

        // Cuando la ruta comienza
        if (nuevoTipo == TipoDeEstadoRuta.VIAJE_COMENZADO) {
            ruta.getParadas().forEach(parada -> {
                Entrega entrega = parada.getEntrega();
                if (entrega != null) {
                    ActualizarEstadoEntregaRequest entregaRequest = new ActualizarEstadoEntregaRequest(
                            "EN_CAMINO",
                            request.responsableDelCambio() + " inició la ruta.",
                            LocalDate.now());
                    entregaService.actualizarEstado(entrega.getId(), entregaRequest);
                }
            });
            try {
                inicioRutaEventPublisher.publicarInicioRuta(ruta);
            } catch (Exception e) {
                // Si falla la publicación a RabbitMQ (por ej. colas no configuradas), no rompemos la operación de estado
            }
        }

        // Si la ruta finaliza, manejamos las entregas que no fueron exitosas
        if (nuevoTipo == TipoDeEstadoRuta.VIAJE_FINALIZADO) {
            ruta.getParadas().forEach(parada -> {
                Entrega entrega = parada.getEntrega();
                if (entrega != null) {
                    TipoDeEstadoEntrega estadoActual = entrega.getEstadosEntrega().isEmpty() ? null
                            : entrega.getEstadosEntrega().getLast().getTipoEstadoEntrega();

                    if (estadoActual == TipoDeEstadoEntrega.EN_CAMINO) {
                        // El chofer no pudo terminar la ruta (entregas que quedaron pendientes)
                        ActualizarEstadoEntregaRequest entregaRequest = new ActualizarEstadoEntregaRequest(
                                "PENDIENTE",
                                "El chofer no pudo terminar la ruta. Vuelve a estar pendiente para planificación.",
                                LocalDate.now());
                        entregaService.actualizarEstado(entrega.getId(), entregaRequest);
                    } else if (estadoActual == TipoDeEstadoEntrega.NO_RECIBIDA) {
                        // El intento de entrega falló (no recibida), por lo que vuelven al depósito
                        ActualizarEstadoEntregaRequest entregaRequest = new ActualizarEstadoEntregaRequest(
                                "EN_DEPOSITO",
                                "Devolución automática al depósito tras intento fallido de entrega.",
                                LocalDate.now());
                        entregaService.actualizarEstado(entrega.getId(), entregaRequest);
                    }
                }
            });
        }

        repository.guardar(ruta);
        return mapearRutaResponse(ruta);
    }

    @Override
    public RutaResponse mapearRutaResponse(Ruta ruta) {
        String estadoActual = ruta.getEstadosDeLaRuta().isEmpty() ? "SIN_ESTADO"
                : ruta.getEstadosDeLaRuta().getLast().getTipoDeEstadoRuta().toString();

        List<ParadaResponse> paradas = ruta.getParadas().stream()
                .map(this::mapearParadaResponse)
                .collect(Collectors.toList());

        return new RutaResponse(
                ruta.getId(),
                ruta.getDistanciaARecorrer(),
                ruta.getFechaDeComienzoDeViaje(),
                ruta.getFechaFinDeViaje(),
                estadoActual,
                paradas);
    }

    private ParadaResponse mapearParadaResponse(Parada parada) {
        EntregaResponse entregaResponse = null;
        if (parada.getEntrega() != null) {
            entregaResponse = mapearEntrega(parada.getEntrega());
        }

        return new ParadaResponse(
                parada.getId(),
                parada.getNumeroDeParada(),
                parada.getFechaEntrega(),
                parada.getEntrega() != null? parada.getEntrega().getRazonSocialEntidadBeneficiaria(): null,
                entregaResponse);
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

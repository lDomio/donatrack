package ar.edu.utn.frba.dds.logistica.main.services.impl;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.*;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.*;
import ar.edu.utn.frba.dds.logistica.main.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.DonacionesClient;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.OptimizadorRutasAdapter;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.DeliveryYDonaciones;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.PlanificadorRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.TipoDeEstadoDonacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.EstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.TipoDeEstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Parada;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.PlanificadorDeRutasDeEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import ar.edu.utn.frba.dds.logistica.main.repositories.*;
import ar.edu.utn.frba.dds.logistica.main.services.PlanificadorDeRutasService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PlanificadorDeRutasServiceImpl implements PlanificadorDeRutasService {
    private final CamionRepository camionRepository;
    private final PlanificadorRutasRepository planificadorRutasRepository;
    private final EntidadBeneficiariaRepository entidadBeneficiariaRepository;
    private final DonacionRepository donacionRepository;
    private final EntregaRepository entregaRepository;
    private final RutaRepository rutaRepository;
    private final DonacionesClient donacionesClient;
    @Getter
    private final PlanificadorDeRutasDeEntrega planificadorDominio;
    private final String warehouseAddress;
    private final Double warehouseLatitude;
    private final Double warehouseLongitude;
    private final Integer horarioComienzo;
    private final Integer horarioFin;

    @org.springframework.beans.factory.annotation.Autowired
    public PlanificadorDeRutasServiceImpl(
            CamionRepository camionRepository,
            PlanificadorRutasRepository planificadorRutasRepository,
            EntidadBeneficiariaRepository entidadBeneficiariaRepository,
            DonacionRepository donacionRepository,
            EntregaRepository entregaRepository,
            RutaRepository rutaRepository,
            DonacionesClient donacionesClient,
            OptimizadorRutasAdapter optimizadorRutasAdapter,
            @Value("${warehouse.address}") String warehouseAddress,
            @Value("${warehouse.latitude}") Double warehouseLatitude,
            @Value("${warehouse.longitude}") Double warehouseLongitude,
            @Value("${horario.comienzo}") Integer horarioComienzo,
            @Value("${horario.fin}") Integer horarioFin) {
        this.planificadorRutasRepository = planificadorRutasRepository;
        this.camionRepository = camionRepository;
        this.entidadBeneficiariaRepository = entidadBeneficiariaRepository;
        this.donacionRepository = donacionRepository;
        this.entregaRepository = entregaRepository;
        this.rutaRepository = rutaRepository;
        this.donacionesClient = donacionesClient;
        this.planificadorDominio = new PlanificadorDeRutasDeEntrega(optimizadorRutasAdapter, 100);
        this.warehouseAddress = warehouseAddress;
        this.warehouseLatitude = warehouseLatitude;
        this.warehouseLongitude = warehouseLongitude;
        this.horarioComienzo = horarioComienzo;
        this.horarioFin = horarioFin;
    }

    public PlanificadorDeRutasServiceImpl(
            CamionRepository camionRepository,
            PlanificadorRutasRepository planificadorRutasRepository,
            EntidadBeneficiariaRepository entidadBeneficiariaRepository,
            DonacionRepository donacionRepository,
            EntregaRepository entregaRepository,
            RutaRepository rutaRepository,
            OptimizadorRutasAdapter optimizadorRutasAdapter,
            String warehouseAddress,
            Double warehouseLatitude,
            Double warehouseLongitude,
            Integer horarioComienzo,
            Integer horarioFin) {
        this(camionRepository, planificadorRutasRepository, entidadBeneficiariaRepository,
                donacionRepository, entregaRepository, rutaRepository, null,
                optimizadorRutasAdapter, warehouseAddress, warehouseLatitude, warehouseLongitude,
                horarioComienzo, horarioFin);
    }

    public void setMaximoPorLote(int maximoPorLote) {
        this.planificadorDominio.setMaximoPorLote(maximoPorLote);
    }

    @Override
    @Async
    public void procesarRutas(RoutingResult rutas) {
        PlanificadorRutaRequest planificadorRutaRequest = planificadorRutasRepository.findById(rutas.request_id())
                .orElse(null);
        if (planificadorRutaRequest == null) {
            return;
        }
        List<DeliveryYDonaciones> deliveriesYDonaciones = planificadorRutaRequest.getDeliveriesYDonaciones();

        rutas.data().routes().forEach(route -> {
            List<Parada> paradas = new ArrayList<>();

            route.stops().forEach(stop -> {
                DeliveryYDonaciones deliveryActual = deliveriesYDonaciones.stream()
                        .filter(d -> Objects.equals(d.getDelivery().deliveryCode(), stop.deliveryCode()))
                        .findFirst().orElse(null);

                if (deliveryActual != null) {
                    String razonSocial = (entidadBeneficiariaRepository.findById(deliveryActual.getDonaciones().getFirst().getEntidadId()).orElseThrow(() -> new ResourceNotFoundException("No se encontró la entidad de la parada"))).getRazonSocial();
                    Entrega entrega = planificadorDominio.crearEntrega(deliveryActual.getDelivery(),
                            deliveryActual.getDonaciones(), razonSocial);
                    entrega.agregarEstado(new EstadoEntrega("Asignada a ruta", LocalDate.now(), TipoDeEstadoEntrega.PENDIENTE));
                    entregaRepository.guardar(entrega);

                    Parada parada = planificadorDominio.crearParada(stop, entrega);
                    parada.setId((long) stop.stopNumber());
                    paradas.add(parada);

                    // Marcamos las donaciones de la entrega como asignadas a ruta
                    if (deliveryActual.getDonaciones() != null) {
                        deliveryActual.getDonaciones().forEach(d -> {
                            Donacion donacionDb = donacionRepository.findById(d.getId()).orElse(d);
                            donacionDb.cambiarEstado(TipoDeEstadoDonacion.ASIGNADA_A_RUTA);
                            donacionRepository.guardar(donacionDb);
                            d.cambiarEstado(TipoDeEstadoDonacion.ASIGNADA_A_RUTA);
                            try {
                                if (donacionesClient != null) {
                                    donacionesClient.actualizarEstado(d.getId(), "LISTA_PARA_ENTREGAR", "Asignada a ruta de entrega", "Sistema Logistica");
                                }
                            } catch (Exception e) {
                                System.out.println("Aviso al actualizar estado a LISTA_PARA_ENTREGAR en donaciones-service: " + e.getMessage());
                            }
                        });
                    }
                }
            });

            Ruta ruta = planificadorDominio.crearRuta(route, paradas);
            rutaRepository.guardar(ruta);

            camionRepository.findById(route.truckId()).ifPresent(c -> {
                c.agregarRuta(ruta);
                camionRepository.guardar(c);
            });
        });

        tratarNoAsignados(rutas.data().unassignedDeliveries(), deliveriesYDonaciones);
        planificadorRutasRepository.eliminar(rutas.request_id());

        System.out.println("Camiones despues de la generacion de rutas");
        camionRepository.findAll().forEach(System.out::println);
        System.out.println("Rutas despues de la generacion de rutas");
        rutaRepository.findAll().forEach(System.out::println);
    }

    private void tratarNoAsignados(List<UnassignedDelivery> noAsignados,
            List<DeliveryYDonaciones> deliveriesYDonaciones) {
        if (noAsignados == null)
            return;
        noAsignados.forEach(deliverySinAsignar -> {
            deliveriesYDonaciones.stream()
                    .filter(delivery -> Objects.equals(delivery.getDelivery().deliveryCode(),
                            deliverySinAsignar.deliveryCode()))
                    .findFirst()
                    .ifPresent(deliveryYDonaciones -> pasarPedirRutaATrue(deliveryYDonaciones.getDonaciones()));
        });
    }

    @Override
    public void tratarPedidosNoRecibidos() {
        planificadorRutasRepository.findAll().forEach(p -> {
            System.out.println("pido estado de " + p.getRequestId());
            PollingRoutingResult result = planificadorDominio.consultarEstado(p.getRequestId());
            if (result != null) {
                System.out.println("El estado es " + result.status());
                if ("COMPLETED".equals(result.status())) {
                    this.procesarRutas(this.toDtoRoutingResult(result, p.getRequestId()));
                } else if ("FAILED".equals(result.status())) {
                    System.out.println("La planificacion de rutas fallo: "
                            + (result.error() != null ? result.error().message() : ""));
                    p.getDeliveriesYDonaciones().forEach(d -> pasarPedirRutaATrue(d.getDonaciones()));
                    planificadorRutasRepository.eliminar(p.getRequestId());
                }
            }
        });
    }

    private void pasarPedirRutaATrue(List<Donacion> donaciones) {
        donaciones.forEach(donacion -> {
            donacion.cambiarEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR);
            donacionRepository.guardar(donacion);
        });
    }

    private RoutingResult toDtoRoutingResult(PollingRoutingResult result, String requestId) {
        return new RoutingResult(
                null,
                "routing-completed",
                requestId,
                LocalDateTime.now(),
                result.result());
    }

    @Override
    public void generarRutas() {
        List<Donacion> todasLasDonaciones = donacionRepository.findAll();
        boolean hayPendientesDePesaje = todasLasDonaciones.stream()
                .anyMatch(d -> d.getEstado() == TipoDeEstadoDonacion.PENDIENTE_DE_PESAJE);
        List<Donacion> donacionesListas = todasLasDonaciones.stream()
                .filter(Donacion::estaListaParaPlanificar)
                .toList();

        // Esperar a que las donaciones asignadas hayan sido pesadas
        if (hayPendientesDePesaje || donacionesListas.isEmpty()) {
            return;
        }

        // Filtrar únicamente camiones que estén en el depósito y no tengan una ruta activa sin finalizar
        List<Camion> camionesDisponibles = camionRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstaEnDeposito()))
                .filter(c -> !c.tieneRutaActiva())
                .sorted((c1, c2) -> {
                    if ("ABC-123".equalsIgnoreCase(c1.getPatente())) return -1;
                    if ("ABC-123".equalsIgnoreCase(c2.getPatente())) return 1;
                    return 0;
                })
                .toList();

        if (camionesDisponibles.isEmpty()) {
            System.out.println("No hay camiones disponibles en depósito para planificar rutas.");
            return;
        }

        double pesoTotalListo = donacionesListas.stream()
                .mapToDouble(d -> d.getPesoKg() != null ? d.getPesoKg() : 0.0)
                .sum();
        double volumenTotalListo = donacionesListas.stream()
                .mapToDouble(d -> d.getVolumen() != null ? d.getVolumen() : 0.0)
                .sum();

        Camion primerCamion = camionesDisponibles.getFirst();
        List<Camion> camionesParaPlanificar = (primerCamion.getCapacidadEnKg() >= pesoTotalListo
                && primerCamion.getVolumen() >= volumenTotalListo)
                ? List.of(primerCamion)
                : camionesDisponibles;

        List<EntidadBeneficiaria> entidadesBeneficiarias = entidadBeneficiariaRepository.findAll();

        List<PlanificadorRutaRequest> requestsApiRutas = planificadorDominio.generarLotes(camionesParaPlanificar,
                entidadesBeneficiarias);

        requestsApiRutas.forEach(planificadorRutaRequest -> {
            planificadorDominio.solicitarPlanificacion(
                    planificadorRutaRequest,
                    horarioComienzo,
                    horarioFin,
                    warehouseLatitude,
                    warehouseLongitude,
                    warehouseAddress);

            // Las donaciones pasan a estado EN_PLANIFICACION para evitar concurrencia sin locks
            planificadorRutaRequest.getDeliveriesYDonaciones().forEach(d -> d.getDonaciones().forEach(donacion -> {
                donacion.cambiarEstado(TipoDeEstadoDonacion.EN_PLANIFICACION);
                donacionRepository.guardar(donacion);
            }));

            planificadorRutasRepository.guardar(planificadorRutaRequest);
        });

        System.out.println("Rutas pedidas");
    }
}

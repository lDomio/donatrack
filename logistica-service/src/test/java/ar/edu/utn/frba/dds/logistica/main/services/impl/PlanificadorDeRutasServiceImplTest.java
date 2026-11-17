package ar.edu.utn.frba.dds.logistica.main.services.impl;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.*;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.*;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.OptimizadorRutasAdapter;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.DeliveryYDonaciones;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.PlanificadorRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.TipoDeEstadoDonacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Ciudad;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.logistica.main.repositories.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanificadorDeRutasServiceImplTest {

    @Mock private CamionRepository camionRepository;
    @Mock private PlanificadorRutasRepository planificadorRutasRepository;
    @Mock private EntidadBeneficiariaRepository entidadBeneficiariaRepository;
    @Mock private DonacionRepository donacionRepository;
    @Mock private EntregaRepository entregaRepository;
    @Mock private RutaRepository rutaRepository;
    @Mock private OptimizadorRutasAdapter optimizadorRutasAdapter;

    private PlanificadorDeRutasServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PlanificadorDeRutasServiceImpl(
                camionRepository, planificadorRutasRepository, entidadBeneficiariaRepository,
                donacionRepository, entregaRepository, rutaRepository, optimizadorRutasAdapter,
                "Av. Deposito 123", -34.6, -58.4, 8, 18
        );
    }

    // =========================================================================
    // TESTS DE procesarRutas
    // =========================================================================

    @Test
    void procesarRutas_SiNoExisteRequest_NoHaceNada() {
        // Arrange
        RoutingResult result = new RoutingResult(null, "type", "req-123", LocalDateTime.now(), null);
        when(planificadorRutasRepository.findById("req-123")).thenReturn(Optional.empty());

        // Act
        service.procesarRutas(result);

        // Assert
        verify(rutaRepository, never()).guardar(any());
        verify(planificadorRutasRepository, never()).eliminar(anyString());
    }

    @Test
    void procesarRutas_ProcesaCorrectamenteLasRutasAsignadas() {
        // Arrange
        String requestId = "req-123";
        String expectedDeliveryCode = "199";

        Delivery delivery = new Delivery(expectedDeliveryCode, -34.0, -58.0, "Calle Test 123", 10.0, 2.0);
        Donacion mockDonacion = new Donacion();
        mockDonacion.setId(99L); mockDonacion.setPesoKg(10.0); mockDonacion.setVolumen(2.0);
        mockDonacion.setEstado(TipoDeEstadoDonacion.EN_PLANIFICACION);

        DeliveryYDonaciones dvd = new DeliveryYDonaciones(delivery, List.of(mockDonacion));
        PlanificadorRutaRequest request = new PlanificadorRutaRequest(requestId, List.of(dvd), Collections.emptyList());
        when(planificadorRutasRepository.findById(requestId)).thenReturn(Optional.of(request));

        ObjectId camionId = new ObjectId("507f191e810c19729de860ea");
        StopPlan stopPlan = new StopPlan(1, expectedDeliveryCode, LocalDateTime.now().plusHours(1));
        RoutePlan routePlan = new RoutePlan(
                camionId.toHexString(), "route-1", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                15.0, 60, List.of(stopPlan)
        );
        RoutingData routingData = new RoutingData(List.of(routePlan), Collections.emptyList());
        RoutingResult result = new RoutingResult(null, "routing-completed", requestId, LocalDateTime.now(), routingData);

        Camion camion = new Camion();
        camion.setId(camionId);
        when(camionRepository.findById(camionId.toHexString())).thenReturn(Optional.of(camion));

        // Act
        service.procesarRutas(result);

        // Assert
        verify(donacionRepository, times(1)).guardar(mockDonacion);
        assertEquals(TipoDeEstadoDonacion.ASIGNADA_A_RUTA, mockDonacion.getEstado(), "La donacion asignada a ruta debe tener estado ASIGNADA_A_RUTA");

        ArgumentCaptor<Entrega> entregaCaptor = ArgumentCaptor.forClass(Entrega.class);
        verify(entregaRepository, times(1)).guardar(entregaCaptor.capture());
        Entrega entregaGuardada = entregaCaptor.getValue();
        assertEquals("Calle Test 123", entregaGuardada.getDireccionEntidad());
        assertFalse(entregaGuardada.getFueRecibidoPorLaEntidad());

        ArgumentCaptor<Ruta> rutaCaptor = ArgumentCaptor.forClass(Ruta.class);
        verify(rutaRepository, times(1)).guardar(rutaCaptor.capture());
        Ruta rutaGuardada = rutaCaptor.getValue();
        assertEquals(15.0, rutaGuardada.getDistanciaARecorrer());
        assertEquals(1, rutaGuardada.getParadas().size());

        verify(camionRepository, times(1)).guardar(camion);
        assertEquals(1, camion.getRutas().size());
        assertEquals(rutaGuardada, camion.getRutas().get(0));

        verify(planificadorRutasRepository, times(1)).eliminar(requestId);
    }

    @Test
    void procesarRutas_SiHayParadasNoAsignadas_VuelvePedirRutaATrue() {
        // Arrange
        String requestId = "req-unassigned";
        String unassignedCode = "288";

        Delivery delivery = new Delivery(unassignedCode, -34.0, -58.0, "Calle No Asignada 123", 5.0, 1.0);
        Donacion mockDonacion = new Donacion();
        mockDonacion.setId(88L);
        mockDonacion.setEstado(TipoDeEstadoDonacion.EN_PLANIFICACION);

        DeliveryYDonaciones dvd = new DeliveryYDonaciones(delivery, List.of(mockDonacion));
        PlanificadorRutaRequest request = new PlanificadorRutaRequest(requestId, List.of(dvd), Collections.emptyList());
        when(planificadorRutasRepository.findById(requestId)).thenReturn(Optional.of(request));

        UnassignedDelivery unassigned = new UnassignedDelivery(unassignedCode, "Exceso de peso");
        RoutingData routingData = new RoutingData(Collections.emptyList(), List.of(unassigned));
        RoutingResult result = new RoutingResult(null, "routing-completed", requestId, LocalDateTime.now(), routingData);

        // Act
        service.procesarRutas(result);

        // Assert
        assertEquals(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR, mockDonacion.getEstado(), "Las donaciones no asignadas deben volver a LISTA_PARA_PLANIFICAR");
        verify(donacionRepository, times(1)).guardar(mockDonacion);
        verify(planificadorRutasRepository, times(1)).eliminar(requestId);
    }

    @Test
    void procesarRutas_MultiplesCamionesYRutas() {
        // Arrange
        String requestId = "req-multi";
        Delivery d1 = new Delivery("101", -34.0, -58.0, "Dir 1", 10.0, 1.0);
        Donacion don1 = new Donacion(); don1.setId(1L); don1.setPesoKg(10.0); don1.setVolumen(1.0);
        DeliveryYDonaciones dvd1 = new DeliveryYDonaciones(d1, List.of(don1));

        Delivery d2 = new Delivery("202", -34.1, -58.1, "Dir 2", 20.0, 2.0);
        Donacion don2 = new Donacion(); don2.setId(2L); don2.setPesoKg(20.0); don2.setVolumen(2.0);
        DeliveryYDonaciones dvd2 = new DeliveryYDonaciones(d2, List.of(don2));

        PlanificadorRutaRequest request = new PlanificadorRutaRequest(requestId, List.of(dvd1, dvd2), Collections.emptyList());
        when(planificadorRutasRepository.findById(requestId)).thenReturn(Optional.of(request));

        ObjectId id1 = new ObjectId("507f191e810c19729de860e1");
        ObjectId id2 = new ObjectId("507f191e810c19729de860e2");

        StopPlan stop1 = new StopPlan(1, "101", LocalDateTime.now().plusHours(1));
        RoutePlan route1 = new RoutePlan(id1.toHexString(), "r-1", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10.0, 60, List.of(stop1));

        StopPlan stop2 = new StopPlan(1, "202", LocalDateTime.now().plusHours(1));
        RoutePlan route2 = new RoutePlan(id2.toHexString(), "r-2", LocalDateTime.now(), LocalDateTime.now().plusHours(3), 20.0, 120, List.of(stop2));

        RoutingData routingData = new RoutingData(List.of(route1, route2), Collections.emptyList());
        RoutingResult result = new RoutingResult(null, "routing-completed", requestId, LocalDateTime.now(), routingData);

        Camion c1 = new Camion(); c1.setId(id1);
        Camion c2 = new Camion(); c2.setId(id2);
        when(camionRepository.findById(id1.toHexString())).thenReturn(Optional.of(c1));
        when(camionRepository.findById(id2.toHexString())).thenReturn(Optional.of(c2));

        // Act
        service.procesarRutas(result);

        // Assert
        verify(rutaRepository, times(2)).guardar(any(Ruta.class));
        verify(camionRepository, times(1)).guardar(c1);
        verify(camionRepository, times(1)).guardar(c2);
        assertEquals(1, c1.getRutas().size());
        assertEquals(1, c2.getRutas().size());
    }

    // =========================================================================
    // TESTS DE tratarPedidosNoRecibidos
    // =========================================================================

    @Test
    void tratarPedidosNoRecibidos_SiNoHayPedidos_NoHaceNada() {
        // Arrange
        when(planificadorRutasRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        service.tratarPedidosNoRecibidos();

        // Assert
        verify(optimizadorRutasAdapter, never()).consultarEstado(anyString());
    }

    @Test
    void tratarPedidosNoRecibidos_CuandoEstadoEsFailed_VuelvePedirRutaATrueYEliminaRequest() {
        // Arrange
        String requestId = "req-failed";
        Delivery delivery = new Delivery("12", -34.0, -58.0, "Dir", 1.0, 1.0);
        Donacion donacion = new Donacion(); donacion.setId(1L); donacion.setEstado(TipoDeEstadoDonacion.EN_PLANIFICACION);
        DeliveryYDonaciones dvd = new DeliveryYDonaciones(delivery, List.of(donacion));
        PlanificadorRutaRequest req = new PlanificadorRutaRequest(requestId, List.of(dvd), Collections.emptyList());

        when(planificadorRutasRepository.findAll()).thenReturn(List.of(req));

        PollingRoutingResult pollingResult = new PollingRoutingResult(
                requestId, "FAILED", null, null, null, new RoutingError("ROUTING_FAILED", "Error message")
        );

        when(optimizadorRutasAdapter.consultarEstado(requestId)).thenReturn(pollingResult);

        // Act
        service.tratarPedidosNoRecibidos();

        // Assert
        assertEquals(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR, donacion.getEstado(), "Se debe volver a marcar la donacion al fallar la planificacion");
        verify(donacionRepository, times(1)).guardar(donacion);
        verify(planificadorRutasRepository, times(1)).eliminar(requestId);
    }

    @Test
    void tratarPedidosNoRecibidos_CuandoEstadoEsCompleted_ProcesaLasRutas() {
        // Arrange
        String requestId = "req-completed";
        Delivery delivery = new Delivery("12", -34.0, -58.0, "Dir", 1.0, 1.0);
        Donacion d = new Donacion(); d.setId(2L); d.setPesoKg(1.0); d.setVolumen(1.0);
        DeliveryYDonaciones dvd = new DeliveryYDonaciones(delivery, List.of(d));
        PlanificadorRutaRequest req = new PlanificadorRutaRequest(requestId, List.of(dvd), Collections.emptyList());

        when(planificadorRutasRepository.findAll()).thenReturn(List.of(req));
        when(planificadorRutasRepository.findById(requestId)).thenReturn(Optional.of(req));

        RoutingData mockData = new RoutingData(Collections.emptyList(), Collections.emptyList());
        PollingRoutingResult pollingResult = new PollingRoutingResult(
                requestId, "COMPLETED", null, null, mockData, null
        );

        when(optimizadorRutasAdapter.consultarEstado(requestId)).thenReturn(pollingResult);

        // Act
        service.tratarPedidosNoRecibidos();

        // Assert
        verify(planificadorRutasRepository, times(1)).eliminar(requestId);
    }

    // =========================================================================
    // TESTS DE generarRutas
    // =========================================================================

    @Test
    void generarRutas_EnviaPeticionYGestionaLasEntidades() {
        // Arrange
        Camion camion = new Camion();
        camion.setId(new ObjectId()); camion.setCapacidadEnKg(1000.0); camion.setVolumen(50.0);
        when(camionRepository.findAll()).thenReturn(List.of(camion));

        EntidadBeneficiaria entidad = new EntidadBeneficiaria();
        entidad.setId(1L);
        Direccion dir = new Direccion("Calle Beneficiaria", 123, new Ciudad("CABA", null), -34.0, -58.0);
        entidad.setDireccion(dir);

        Donacion donacion = new Donacion();
        donacion.setId(100L); donacion.setEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR);
        donacion.setPesoKg(10.0); donacion.setVolumen(2.0);
        entidad.agregarDonacionAsignada(donacion);

        when(entidadBeneficiariaRepository.findAll()).thenReturn(List.of(entidad));

        // Act
        service.generarRutas();

        // Assert
        verify(donacionRepository, times(1)).guardar(donacion);
        assertEquals(TipoDeEstadoDonacion.EN_PLANIFICACION, donacion.getEstado());

        ArgumentCaptor<PlanificadorRutaRequest> captor = ArgumentCaptor.forClass(PlanificadorRutaRequest.class);
        verify(planificadorRutasRepository, times(1)).guardar(captor.capture());

        PlanificadorRutaRequest guardado = captor.getValue();
        assertNotNull(guardado.getRequestId());
        assertEquals(1, guardado.getDeliveriesYDonaciones().size());
        assertEquals(1, guardado.getTrucks().size());

        verify(optimizadorRutasAdapter, times(1)).solicitarPlanificacion(any(RequestApiRutas.class));
    }

    @Test
    void generarRutas_SoloIncluyeDonacionesValidas() {
        // Arrange
        Camion camion = new Camion();
        camion.setId(new ObjectId()); camion.setCapacidadEnKg(1000.0); camion.setVolumen(50.0);
        when(camionRepository.findAll()).thenReturn(List.of(camion));

        EntidadBeneficiaria entidad = new EntidadBeneficiaria();
        entidad.setId(2L);
        entidad.setDireccion(new Direccion("Calle", 123, new Ciudad("CABA", null), -34.0, -58.0));

        Donacion d1 = new Donacion(); d1.setId(10L); d1.setEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR); d1.setPesoKg(4.0); d1.setVolumen(1.0);
        Donacion d2 = new Donacion(); d2.setId(20L); d2.setEstado(TipoDeEstadoDonacion.PENDIENTE_DE_PESAJE); d2.setPesoKg(4.0); d2.setVolumen(1.0);
        Donacion d3 = new Donacion(); d3.setId(30L); d3.setEstado(TipoDeEstadoDonacion.ASIGNADA_A_RUTA); d3.setPesoKg(4.0); d3.setVolumen(1.0);

        entidad.agregarDonacionAsignada(d1);
        entidad.agregarDonacionAsignada(d2);
        entidad.agregarDonacionAsignada(d3);

        when(entidadBeneficiariaRepository.findAll()).thenReturn(List.of(entidad));

        // Act
        service.generarRutas();

        // Assert
        ArgumentCaptor<PlanificadorRutaRequest> captor = ArgumentCaptor.forClass(PlanificadorRutaRequest.class);
        verify(planificadorRutasRepository, times(1)).guardar(captor.capture());

        List<DeliveryYDonaciones> listado = captor.getValue().getDeliveriesYDonaciones();
        assertEquals(1, listado.size());
        assertEquals("210", listado.get(0).getDelivery().deliveryCode());
        assertEquals(1, listado.get(0).getDonaciones().size());

        verify(optimizadorRutasAdapter, times(1)).solicitarPlanificacion(any(RequestApiRutas.class));
    }

    @Test
    void generarRutas_SeparaEnMultiplesRequestsCuandoLosDeliveriesExcedenElMaximoPorLote() {
        // Arrange
        service.setMaximoPorLote(2);

        Camion camionGrande = new Camion();
        camionGrande.setId(new ObjectId()); camionGrande.setCapacidadEnKg(9000.0); camionGrande.setVolumen(9000.0);
        when(camionRepository.findAll()).thenReturn(List.of(camionGrande));

        List<EntidadBeneficiaria> entidades = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            EntidadBeneficiaria e = new EntidadBeneficiaria();
            e.setId(i);
            e.setDireccion(new Direccion("Calle " + i, (int)i, new Ciudad("CABA", null), -34.0, -58.0));

            Donacion d = new Donacion();
            d.setId(i * 10); d.setEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR);
            d.setPesoKg(10.0); d.setVolumen(1.0);
            e.agregarDonacionAsignada(d);
            entidades.add(e);
        }

        when(entidadBeneficiariaRepository.findAll()).thenReturn(entidades);

        // Act
        service.generarRutas();

        // Assert
        verify(optimizadorRutasAdapter, times(2)).solicitarPlanificacion(any(RequestApiRutas.class));

        ArgumentCaptor<PlanificadorRutaRequest> captor = ArgumentCaptor.forClass(PlanificadorRutaRequest.class);
        verify(planificadorRutasRepository, times(2)).guardar(captor.capture());

        List<PlanificadorRutaRequest> requestsGuardados = captor.getAllValues();
        assertEquals(2, requestsGuardados.size());
        assertEquals(2, requestsGuardados.get(0).getDeliveriesYDonaciones().size(), "El primer lote debe contener 2 deliveries");
        assertEquals(1, requestsGuardados.get(1).getDeliveriesYDonaciones().size(), "El segundo lote debe contener el delivery restante");
    }

    @Test
    void procesarRutas_EscenarioMasivo_MultiplesCamionesZonasDistantesYDistanciasCoherentes() {
        // Arrange
        String requestId = "req-mega-test-999";

        Delivery delCaba = new Delivery("1011", -34.5985, -58.4201, "Av. Medrano 951, CABA", 50.0, 0.5);
        Delivery delBanfield = new Delivery("1022", -34.7444, -58.3961, "Maipú 250, Banfield", 100.0, 1.0);
        Delivery delPilar = new Delivery("1033", -34.4588, -58.9143, "Ruta 8 Km 50, Pilar", 200.0, 2.0);
        Delivery delRechazado = new Delivery("1044", -34.7444, -58.3961, "Maipú 250, Banfield", 5000.0, 50.0);

        Donacion dCaba = new Donacion(); dCaba.setId(1L); dCaba.setPesoKg(50.0); dCaba.setVolumen(0.5);
        Donacion dBanfield = new Donacion(); dBanfield.setId(2L); dBanfield.setPesoKg(100.0); dBanfield.setVolumen(1.0);
        Donacion dPilar = new Donacion(); dPilar.setId(3L); dPilar.setPesoKg(200.0); dPilar.setVolumen(2.0);
        Donacion dRechazada = new Donacion(); dRechazada.setId(4L); dRechazada.setEstado(TipoDeEstadoDonacion.EN_PLANIFICACION); dRechazada.setPesoKg(5000.0); dRechazada.setVolumen(50.0);

        List<DeliveryYDonaciones> dvds = List.of(
                new DeliveryYDonaciones(delCaba, List.of(dCaba)),
                new DeliveryYDonaciones(delBanfield, List.of(dBanfield)),
                new DeliveryYDonaciones(delPilar, List.of(dPilar)),
                new DeliveryYDonaciones(delRechazado, List.of(dRechazada))
        );

        PlanificadorRutaRequest requestOriginal = new PlanificadorRutaRequest(requestId, dvds, Collections.emptyList());
        when(planificadorRutasRepository.findById(requestId)).thenReturn(Optional.of(requestOriginal));

        ObjectId idCaba = new ObjectId("507f191e810c19729de860e3");
        ObjectId idNorte = new ObjectId("507f191e810c19729de860e4");
        Camion camionCabaSur = new Camion(); camionCabaSur.setId(idCaba); camionCabaSur.setPatente("SUR-123");
        Camion camionNorte = new Camion(); camionNorte.setId(idNorte); camionNorte.setPatente("NOR-456");
        when(camionRepository.findById(idCaba.toHexString())).thenReturn(Optional.of(camionCabaSur));
        when(camionRepository.findById(idNorte.toHexString())).thenReturn(Optional.of(camionNorte));

        StopPlan stopCaba = new StopPlan(1, "1011", LocalDateTime.parse("2026-06-20T10:00:00"));
        StopPlan stopBanfield = new StopPlan(2, "1022", LocalDateTime.parse("2026-06-20T11:30:00"));
        RoutePlan routeSur = new RoutePlan(idCaba.toHexString(), "ROUTE-SUR", LocalDateTime.parse("2026-06-20T09:00:00"),
                LocalDateTime.parse("2026-06-20T13:00:00"), 86.5, 240, List.of(stopCaba, stopBanfield));

        StopPlan stopPilar = new StopPlan(1, "1033", LocalDateTime.parse("2026-06-20T09:45:00"));
        RoutePlan routeNorte = new RoutePlan(idNorte.toHexString(), "ROUTE-NOR", LocalDateTime.parse("2026-06-20T09:00:00"),
                LocalDateTime.parse("2026-06-20T10:30:00"), 92.0, 90, List.of(stopPilar));

        UnassignedDelivery unassigned = new UnassignedDelivery("1044", "Excede capacidad máxima de la flota");

        RoutingData routingData = new RoutingData(List.of(routeSur, routeNorte), List.of(unassigned));
        RoutingResult resultApi = new RoutingResult(null, "routing.completed", requestId, LocalDateTime.now(), routingData);

        // Act
        service.procesarRutas(resultApi);

        // Assert
        ArgumentCaptor<Entrega> entregaCaptor = ArgumentCaptor.forClass(Entrega.class);
        verify(entregaRepository, times(3)).guardar(entregaCaptor.capture());
        List<Entrega> entregasGuardadas = entregaCaptor.getAllValues();

        assertTrue(entregasGuardadas.stream().anyMatch(e -> e.getDireccionEntidad().contains("Medrano")));
        assertTrue(entregasGuardadas.stream().anyMatch(e -> e.getDireccionEntidad().contains("Maipú")));
        assertTrue(entregasGuardadas.stream().anyMatch(e -> e.getDireccionEntidad().contains("Ruta 8")));

        ArgumentCaptor<Ruta> rutaCaptor = ArgumentCaptor.forClass(Ruta.class);
        verify(rutaRepository, times(2)).guardar(rutaCaptor.capture());
        List<Ruta> rutasGuardadas = rutaCaptor.getAllValues();

        Ruta rutaSurGuardada = rutasGuardadas.stream().filter(r -> r.getDistanciaARecorrer() == 86.5).findFirst().orElseThrow();
        assertEquals(2, rutaSurGuardada.getParadas().size());

        verify(donacionRepository, times(1)).guardar(dRechazada);
        assertEquals(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR, dRechazada.getEstado());
        verify(planificadorRutasRepository, times(1)).eliminar(requestId);
    }

    @Test
    void generarRutas_FraccionaDeliveriesCuandoLasDonacionesExcedenLaCapacidadMaxDeLosCamiones() {
        // Arrange
        Camion camionLimitado = new Camion();
        camionLimitado.setId(new ObjectId());
        camionLimitado.setCapacidadEnKg(50.0);
        camionLimitado.setVolumen(50.0);
        when(camionRepository.findAll()).thenReturn(List.of(camionLimitado));

        EntidadBeneficiaria entidad = new EntidadBeneficiaria();
        entidad.setId(10L);
        entidad.setDireccion(new Direccion("Av. Corrientes", 1234, new Ciudad("CABA", null), -34.6, -58.4));

        Donacion d1 = new Donacion();
        d1.setId(101L); d1.setEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR);
        d1.setPesoKg(40.0); d1.setVolumen(5.0);

        Donacion d2 = new Donacion();
        d2.setId(102L); d2.setEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR);
        d2.setPesoKg(20.0); d2.setVolumen(5.0);

        entidad.agregarDonacionAsignada(d1);
        entidad.agregarDonacionAsignada(d2);

        when(entidadBeneficiariaRepository.findAll()).thenReturn(List.of(entidad));

        // Act
        service.generarRutas();

        // Assert
        ArgumentCaptor<PlanificadorRutaRequest> captor = ArgumentCaptor.forClass(PlanificadorRutaRequest.class);
        verify(planificadorRutasRepository, times(1)).guardar(captor.capture());

        PlanificadorRutaRequest requestGuardado = captor.getValue();
        List<DeliveryYDonaciones> deliveriesGuardados = requestGuardado.getDeliveriesYDonaciones();

        assertEquals(2, deliveriesGuardados.size(), "Debería haber fraccionado las donaciones en 2 entregas independientes por exceso de capacidad");

        DeliveryYDonaciones primerEnvio = deliveriesGuardados.get(0);
        assertEquals("10101", primerEnvio.getDelivery().deliveryCode());
        assertEquals(40.0, primerEnvio.getDelivery().WeightKg());
        assertEquals(1, primerEnvio.getDonaciones().size());
        assertEquals(101L, primerEnvio.getDonaciones().get(0).getId());

        DeliveryYDonaciones segundoEnvio = deliveriesGuardados.get(1);
        assertEquals("10102", segundoEnvio.getDelivery().deliveryCode());
        assertEquals(20.0, segundoEnvio.getDelivery().WeightKg());
        assertEquals(1, segundoEnvio.getDonaciones().size());
        assertEquals(102L, segundoEnvio.getDonaciones().get(0).getId());

        verify(optimizadorRutasAdapter, times(1)).solicitarPlanificacion(any(RequestApiRutas.class));
    }
}

package ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.PollingRoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutePlan;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.StopPlan;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Delivery;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.RequestApiRutas;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.TimeWindow;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Truck;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Warehouse;
import ar.edu.utn.frba.dds.logistica.main.infrastructure.OptimizadorRutasAdapter;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.DeliveryYDonaciones;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.PlanificadorRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.DonacionEnEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanificadorDeRutasDeEntrega {
    private OptimizadorRutasAdapter optimizadorRutasAdapter;
    private int maximoPorLote = 100;

    public List<PlanificadorRutaRequest> generarLotes(List<Camion> camiones,
            List<EntidadBeneficiaria> entidadesBeneficiarias) {
        List<PlanificadorRutaRequest> requests = new ArrayList<>();
        List<Truck> trucks = camiones.stream().map(this::toDtoTruck).toList();
        List<DeliveryYDonaciones> deliveries = new ArrayList<>();
        for (EntidadBeneficiaria entidad : entidadesBeneficiarias) {
            double peso = 0;
            double volumen = 0;
            List<Donacion> donacionesParaDelivery = new ArrayList<>();
            List<Donacion> donaciones = entidad.getDonacionesAsignadas() != null
                    ? entidad.getDonacionesAsignadas()
                    : List.of();
            for (Donacion donacion : donaciones.stream().filter(Donacion::estaListaParaPlanificar)
                    .toList()) {
                double finalPeso = peso;
                double finalVolumen = volumen;
                if (camiones.stream().anyMatch(c -> c.getCapacidadEnKg() >= finalPeso + donacion.getPesoKg()
                        && c.getVolumen() >= finalVolumen + donacion.getVolumen())) {
                    donacionesParaDelivery.add(donacion);
                    peso += donacion.getPesoKg();
                    volumen += donacion.getVolumen();
                } else {
                    if (!donacionesParaDelivery.isEmpty()) {
                        deliveries.add(new DeliveryYDonaciones(
                                toDtoDelivery(entidad, donacionesParaDelivery),
                                donacionesParaDelivery));
                    }
                    donacionesParaDelivery = new ArrayList<>();
                    donacionesParaDelivery.add(donacion);
                    peso = donacion.getPesoKg();
                    volumen = donacion.getVolumen();
                }
            }
            if (!donacionesParaDelivery.isEmpty()) {
                deliveries.add(new DeliveryYDonaciones(
                        toDtoDelivery(entidad, donacionesParaDelivery),
                        donacionesParaDelivery));
            }
        }

        int deliveryActual = 0;
        while (deliveryActual < deliveries.size()) {
            int espacioRestante = deliveries.size() - deliveryActual;
            int siguienteCorte = Math.min(maximoPorLote, espacioRestante);
            requests.add(new PlanificadorRutaRequest(
                    UUID.randomUUID().toString(),
                    deliveries.subList(deliveryActual, deliveryActual + siguienteCorte),
                    trucks));
            deliveryActual += siguienteCorte;
        }
        return requests;
    }

    public void solicitarPlanificacion(PlanificadorRutaRequest planificadorRutaRequest,
            Integer horarioComienzo, Integer horarioFin,
            Double warehouseLatitude, Double warehouseLongitude, String warehouseAddress) {
        RequestApiRutas apiRequest = crearRequestApi(
                planificadorRutaRequest,
                horarioComienzo,
                horarioFin,
                warehouseLatitude,
                warehouseLongitude,
                warehouseAddress);
        if (optimizadorRutasAdapter != null) {
            optimizadorRutasAdapter.solicitarPlanificacion(apiRequest);
        }
    }

    public PollingRoutingResult consultarEstado(String requestId) {
        if (optimizadorRutasAdapter != null) {
            return optimizadorRutasAdapter.consultarEstado(requestId);
        }
        return null;
    }

    public RequestApiRutas crearRequestApi(PlanificadorRutaRequest planificadorRutaRequest,
            Integer horarioComienzo, Integer horarioFin,
            Double warehouseLatitude, Double warehouseLongitude, String warehouseAddress) {
        return new RequestApiRutas(
                planificadorRutaRequest.getRequestId(),
                new TimeWindow(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(horarioComienzo, 0, 0))
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")),
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(horarioFin, 0, 0))
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))),
                new Warehouse(
                        warehouseLatitude,
                        warehouseLongitude,
                        warehouseAddress),
                planificadorRutaRequest.getDeliveriesYDonaciones().stream().map(DeliveryYDonaciones::getDelivery)
                        .toList(),
                planificadorRutaRequest.getTrucks());
    }

    public Parada crearParada(StopPlan stop, Entrega entrega) {
        Parada parada = new Parada();
        parada.setNumeroDeParada(stop.stopNumber());
        parada.setFechaEntrega(stop.estimatedArrivalTime());
        parada.setEntrega(entrega);
        return parada;
    }

    public Ruta crearRuta(RoutePlan route, List<Parada> paradas) {
        Ruta ruta = new Ruta();
        ruta.setDistanciaARecorrer(route.totalDistanceKm());
        ruta.setFechaDeComienzoDeViaje(route.estimatedStartTime());
        ruta.setFechaFinDeViaje(route.estimatedEndTime());
        ruta.agregarEstado(new EstadoDeLaRuta(LocalDate.now(), TipoDeEstadoRuta.RUTA_ASIGNADA));
        paradas.forEach(ruta::agregarParada);
        return ruta;
    }

    public Entrega crearEntrega(Delivery delivery, List<Donacion> donaciones, String razonSocial) {
        Entrega entrega = new Entrega();
        entrega.setDireccionEntidad(delivery.address());
        entrega.setLatitud(delivery.latitude());
        entrega.setLongitud(delivery.longitude());
        entrega.setFueRecibidoPorLaEntidad(false);
        entrega.setRazonSocialEntidadBeneficiaria(razonSocial);
        for (Donacion donacion : donaciones) {
            DonacionEnEntrega donacionEnEntrega = new DonacionEnEntrega(
                    donacion.getId(),
                    donacion.getSubcategoria(),
                    donacion.getCantidad(),
                    donacion.getPesoKg(),
                    donacion.getDonanteId());

            entrega.agregarDonacion(donacionEnEntrega);
        }
        return entrega;
    }

    private Truck toDtoTruck(Camion camion) {
        return new Truck(
                camion.getId() != null ? camion.getId().toHexString() : "",
                camion.getCapacidadEnKg(),
                camion.getVolumen());
    }

    private Delivery toDtoDelivery(EntidadBeneficiaria entidad, List<Donacion> donaciones) {
        String deliveryCode;
        if (donaciones.isEmpty()) {
            deliveryCode = entidad.getId().toString();
        } else {
            deliveryCode = entidad.getId().toString() + donaciones.getFirst().getId();
        }
        return new Delivery(
                deliveryCode,
                entidad.getDireccion().getLatitud(),
                entidad.getDireccion().getLongitud(),
                entidad.getDireccion().getCalle() + " " + entidad.getDireccion().getAltura() + ", "
                        + entidad.getDireccion().getCiudad().getNombre(),
                donaciones.stream().mapToDouble(Donacion::getPesoKg).sum(),
                donaciones.stream().mapToDouble(Donacion::getVolumen).sum());
    }
}

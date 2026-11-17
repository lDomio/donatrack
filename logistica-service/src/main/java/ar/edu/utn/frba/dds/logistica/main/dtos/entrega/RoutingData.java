package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import java.util.List;

public record RoutingData(
    List<RoutePlan> routes,
    List<UnassignedDelivery> unassignedDeliveries
) {}

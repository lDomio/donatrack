package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record RoutePlan(
    String truckId,
    String assignedRouteId,
    LocalDateTime estimatedStartTime,
    LocalDateTime estimatedEndTime,
    Double totalDistanceKm,
    Integer totalDurationMins,
    List<StopPlan> stops
) {}

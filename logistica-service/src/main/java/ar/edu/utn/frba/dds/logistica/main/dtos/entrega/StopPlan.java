package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;

public record StopPlan(
    Integer stopNumber,
    String deliveryCode,
    LocalDateTime estimatedArrivalTime
) {}

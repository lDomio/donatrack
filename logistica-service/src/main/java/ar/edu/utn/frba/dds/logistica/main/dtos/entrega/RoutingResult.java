package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record RoutingResult(
    String event_id,
    String event_type,
    String request_id,
    LocalDateTime timestamp,
    RoutingData data
) {}

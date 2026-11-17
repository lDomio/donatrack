package ar.edu.utn.frba.dds.logistica.main.dtos.entrega;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PollingRoutingResult(
        @JsonAlias({"requestId", "request_id"})
        String request_id,
        String status,
        String createdAt,
        String updatedAt,
        RoutingData result,
        RoutingError error
) {
}


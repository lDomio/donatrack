package ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas;

import java.util.List;

public record RequestApiRutas(
        String requestId,
        TimeWindow timeWindow,
        Warehouse warehouse,
        List<Delivery> deliveries,
        List<Truck> trucks
) {
}


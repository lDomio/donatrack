package ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas;

public record Truck(
        String truckId,
        Double weightCapacityKg,
        Double volumeCapacityM3
) {
}


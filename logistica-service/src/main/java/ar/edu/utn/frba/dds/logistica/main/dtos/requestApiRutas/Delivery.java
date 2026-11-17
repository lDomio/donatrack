package ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas;

public record Delivery(
        String deliveryCode,
        Double latitude,
        Double longitude,
        String address,
        Double WeightKg,
        Double VolumeM3
) {
}


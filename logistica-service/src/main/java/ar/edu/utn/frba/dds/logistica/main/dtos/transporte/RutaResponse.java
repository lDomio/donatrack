package ar.edu.utn.frba.dds.logistica.main.dtos.transporte;

import java.time.LocalDateTime;
import java.util.List;

public record RutaResponse(
    Long id,
    Double distanciaARecorrer,
    LocalDateTime fechaDeComienzoDeViaje,
    LocalDateTime fechaFinDeViaje,
    String estadoActual,
    List<ParadaResponse> paradas
) {}

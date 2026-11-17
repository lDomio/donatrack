package ar.edu.utn.frba.dds.logistica.main.infrastructure;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.PollingRoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.RequestApiRutas;

public interface OptimizadorRutasAdapter {
    void solicitarPlanificacion(RequestApiRutas request);
    PollingRoutingResult consultarEstado(String requestId);
}
package ar.edu.utn.frba.dds.logistica.main.services;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutingResult;

public interface PlanificadorDeRutasService {
    void generarRutas();
    void procesarRutas(RoutingResult result);
    void tratarPedidosNoRecibidos();
}


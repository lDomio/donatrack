package ar.edu.utn.frba.dds.logistica.main.services;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.ActualizarEstadoRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.RutaResponse;


import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import java.util.List;

public interface RutaService {
    List<RutaResponse> obtenerTodas();
    RutaResponse buscarPorId(Long id);
    RutaResponse actualizarEstado(Long id, ActualizarEstadoRutaRequest request);
    void procesarResultadoRouting(RoutingResult request);
    RutaResponse mapearRutaResponse(Ruta ruta);
}

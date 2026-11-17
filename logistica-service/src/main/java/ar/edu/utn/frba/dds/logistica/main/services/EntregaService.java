package ar.edu.utn.frba.dds.logistica.main.services;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.ActualizarEstadoEntregaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.EntregaResponse;
import java.util.List;

public interface EntregaService {
    List<EntregaResponse> listarPendientes();
    EntregaResponse buscarPorId(Long id);
    EntregaResponse actualizarEstado(Long id, ActualizarEstadoEntregaRequest request);
    // TODO: Persona 1/6 implementarán marcarRecepcion
}

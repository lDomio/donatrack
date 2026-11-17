package ar.edu.utn.frba.dds.logistica.main.services;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.logistica.main.dtos.camion.CamionRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.camion.CamionResponse;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.ActualizarEstadoEntregaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.RutaResponse;
import java.util.List;
import java.util.Optional;

public interface CamionService {
    List<CamionResponse> listarTodos();
    CamionResponse buscarPorId(String id);
    Optional<CamionResponse> buscarPorIdUsuario(Long idUsuario);
    CamionResponse agregarCamion(CamionRequest request);
    CamionResponse modificarChofer(String id, String nuevoChofer);
    CamionResponse modificarEstado(String id, Boolean estaEnDeposito);
    CamionResponse iniciarViaje(String id);
    void eliminarCamion(String id);
    void validarRutaDeCamion(Long idUsuarioAuth, Long idRuta);
    void validarUsuarioDeCamion(Long idUsuarioAuth, String idCamion);
    void validarSiCamionPuedeCancelarEntrega(Long idUsuarioAuth, Long idEntrega, ActualizarEstadoEntregaRequest request);
}

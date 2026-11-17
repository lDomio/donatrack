package ar.edu.utn.frba.dds.logistica.main.services.impl;

import ar.edu.utn.frba.dds.logistica.main.exception.UnauthorizedException;
import ar.edu.utn.frba.dds.logistica.main.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.logistica.main.dtos.camion.CamionRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.camion.CamionResponse;
import ar.edu.utn.frba.dds.logistica.main.dtos.transporte.ActualizarEstadoRutaRequest;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.TipoDeEstadoRuta;
import ar.edu.utn.frba.dds.logistica.main.repositories.CamionRepository;
import ar.edu.utn.frba.dds.logistica.main.services.CamionService;
import ar.edu.utn.frba.dds.logistica.main.services.RutaService;
import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.ActualizarEstadoEntregaRequest;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Parada;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.TipoDeEstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.services.EntregaService;
import java.util.Objects;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CamionServiceImpl implements CamionService {
    private final CamionRepository repository;
    private final RutaService rutaService;
    private final EntregaService entregaService;

    public CamionServiceImpl(CamionRepository repository, @Lazy RutaService rutaService, @Lazy EntregaService entregaService) {
        this.repository = repository;
        this.rutaService = rutaService;
        this.entregaService = entregaService;
    }

    @Override
    public List<CamionResponse> listarTodos() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CamionResponse buscarPorId(String id) {
        return repository.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Camión no encontrado con ID: " + id));
    }

    @Override
    public CamionResponse agregarCamion(CamionRequest request) {
        Camion camion = new Camion();
        camion.setPatente(request.getPatente());
        camion.setCapacidadEnKg(request.getCapacidadEnKg());
        camion.setVolumen(request.getVolumen());
        camion.setAltura(request.getAltura());
        camion.setNombreChofer(request.getNombreChofer());
        camion.setDireccion(request.getDireccion());
        camion.setLatitud(request.getLatitud());
        camion.setLongitud(request.getLongitud());
        camion.setIdUsuario(request.getIdUsuario());
        camion.setEstaEnDeposito(true);

        repository.guardar(camion);
        return mapToResponse(camion);
    }

    @Override
    public CamionResponse modificarChofer(String id, String nuevoChofer) {
        Camion camion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camión no encontrado con ID: " + id));
        camion.setNombreChofer(nuevoChofer);
        repository.guardar(camion);
        return mapToResponse(camion);
    }

    @Override
    public CamionResponse modificarEstado(String id, Boolean estaEnDeposito) {
        Camion camion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camión no encontrado con ID: " + id));

        camion.setEstaEnDeposito(estaEnDeposito);

        if (Boolean.FALSE.equals(estaEnDeposito)) {
            // Salida del depósito / Iniciar viaje:
            if (camion.getRutas() != null) {
                for (Ruta ruta : camion.getRutas()) {
                    if (ruta != null && ruta.getId() != null) {
                        try {
                            rutaService.actualizarEstado(ruta.getId(), new ActualizarEstadoRutaRequest("VIAJE_COMENZADO", "Chofer"));
                        } catch (Exception e) {
                            // Si ya está iniciada o ocurre un error menor, continuar
                        }
                    }
                }
            }
        } else if (Boolean.TRUE.equals(estaEnDeposito)) {
            // Regreso al depósito / Finalizar viaje:
            if (camion.getRutas() != null) {
                for (Ruta ruta : camion.getRutas()) {
                    if (ruta != null && ruta.getId() != null) {
                        // Cancelar las paradas que hayan quedado pendientes por falta de tiempo
                        if (ruta.getParadas() != null) {
                            for (Parada parada : ruta.getParadas()) {
                                Entrega entrega = parada.getEntrega();
                                if (entrega != null) {
                                    TipoDeEstadoEntrega estadoActual = entrega.getEstadosEntrega().isEmpty() ? null
                                            : entrega.getEstadosEntrega().getLast().getTipoEstadoEntrega();
                                    if (estadoActual != TipoDeEstadoEntrega.EXITOSA
                                            && estadoActual != TipoDeEstadoEntrega.NO_RECIBIDA
                                            && estadoActual != TipoDeEstadoEntrega.CANCELADA) {
                                        try {
                                            entregaService.actualizarEstado(entrega.getId(),
                                                    new ActualizarEstadoEntregaRequest("NO_RECIBIDA", "Falta de tiempo", LocalDate.now()));
                                        } catch (Exception ex) {
                                            // continuar
                                        }
                                    }
                                }
                            }
                        }
                        try {
                            rutaService.actualizarEstado(ruta.getId(), new ActualizarEstadoRutaRequest("VIAJE_FINALIZADO", "Chofer"));
                        } catch (Exception e) {
                            // continuar
                        }
                    }
                }
            }
        }

        repository.guardar(camion);
        return mapToResponse(camion);
    }

    @Override
    public CamionResponse iniciarViaje(String id) {
        return modificarEstado(id, false);
    }

    @Override
    public void eliminarCamion(String id) {
        Camion camion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camión no encontrado con ID: " + id));

        // Validación 1: Corroborar que el camión no esté en viaje (debe estar en depósito)
        if (camion.getEstaEnDeposito() != null && !camion.getEstaEnDeposito()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar el camión porque se encuentra en viaje.");
        }

        // Validación 2: Corroborar que no tenga entregas asociadas
        boolean tieneEntregas = camion.getRutas() != null && camion.getRutas().stream()
                .anyMatch(ruta -> ruta != null && ruta.getParadas() != null && ruta.getParadas().stream()
                        .anyMatch(parada -> parada.getEntrega() != null));

        if (tieneEntregas) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar el camión porque tiene entregas asociadas.");
        }

        repository.eliminar(id);
    }

    @Override
    public void validarRutaDeCamion(Long idUsuarioAuth, Long idRuta) {
        Camion camion = repository.findByIdUsuario(idUsuarioAuth).orElseThrow(()->new ResourceNotFoundException("No se encontró el camión."));
        if(camion.getRutas().stream().map(Ruta::getId).noneMatch(id-> Objects.equals(id, idRuta))){
            throw new UnauthorizedException("Solo puede ver sus rutas.");
        }
    }

    @Override
    public void validarUsuarioDeCamion(Long idUsuarioAuth, String idCamion) {
        Camion camion = repository.findByIdUsuario(idUsuarioAuth).orElseThrow(()-> new ResourceNotFoundException("No se encontró el camión."));
        String camionIdHex = camion.getId() != null ? camion.getId().toHexString() : "";
        if(!camionIdHex.equals(idCamion) && (camion.getId() == null || !camion.getId().toString().equals(idCamion))){
            throw new UnauthorizedException("No tiene permiso para operar sobre otros camiones que no sean el suyo.");
        }
    }

    @Override
    public void validarSiCamionPuedeCancelarEntrega(Long idUsuarioAuth, Long idEntrega, ActualizarEstadoEntregaRequest request) {

        if(!TipoDeEstadoEntrega.valueOf( request.nuevoEstado()).equals(TipoDeEstadoEntrega.CANCELADA)){
            throw new UnauthorizedException("El camionero solo puede pasar el estado de la entrega a cancelado.");
        }

        Camion camion = repository.findByIdUsuario(idUsuarioAuth).orElseThrow(()-> new ResourceNotFoundException("No se encontró el camión."));

        Ruta ruta = camion.getRutas().stream().filter(r->r.getUltimoEstado() == TipoDeEstadoRuta.VIAJE_COMENZADO).findFirst().orElseThrow(()->new ResourceNotFoundException("El camión no tiene viajes iniciados"));
        Parada parada = ruta.getParadas().stream().filter(r -> Objects.equals(r.getEntrega().getId(), idEntrega)).findFirst().orElseThrow(()->new UnauthorizedException("El camionero no tiene esa entrega asignada."));

        if(parada.getEntrega().getFueRecibidoPorLaEntidad()){
            throw new UnauthorizedException("El camionero no puede cancelar una entrega que ya fue recibida.");
        }

    }

    @Override
    public Optional<CamionResponse> buscarPorIdUsuario(Long idUsuario) {
        return repository.findByIdUsuario(idUsuario).map(this::mapToResponse);
    }

    private CamionResponse mapToResponse(Camion camion) {
        List<Long> rutasIds = camion.getRutas() != null ? camion.getRutas().stream()
                .filter(r -> r != null && r.getId() != null)
                .map(Ruta::getId)
                .collect(Collectors.toList()) : List.of();
        Ruta rutaActiva = camion.getRutaActiva().orElse(null);

        return new CamionResponse(
                camion.getId() != null ? camion.getId().toHexString() : null,
                camion.getPatente(),
                camion.getCapacidadEnKg(),
                camion.getVolumen(),
                camion.getAltura(),
                camion.getEstaEnDeposito(),
                camion.getNombreChofer(),
                camion.getDireccion(),
                camion.getLatitud(),
                camion.getLongitud(),
                camion.getIdUsuario(),
                rutasIds,
                rutaActiva != null? rutaService.mapearRutaResponse(rutaActiva):null
        );
    }
}

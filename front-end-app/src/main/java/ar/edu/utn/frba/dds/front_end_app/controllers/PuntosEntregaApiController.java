package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.CasoExitoDto;
import ar.edu.utn.frba.dds.front_end_app.dto.PuntoEntregaDto;
import ar.edu.utn.frba.dds.front_end_app.services.DonacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PuntosEntregaApiController {

    private static final Logger log = LoggerFactory.getLogger(PuntosEntregaApiController.class);
    private final DonacionService donacionService;

    public PuntosEntregaApiController(DonacionService donacionService) {
        this.donacionService = donacionService;
    }

    @GetMapping({"/puntos-entrega", "/donaciones-publicas"})
    public ResponseEntity<List<PuntoEntregaDto>> obtenerPuntosEntrega() {
        log.info("[PuntosEntregaApi] GET /api/puntos-entrega solicitado");
        List<PuntoEntregaDto> puntos = new ArrayList<>();

        // 1. Cargar casos de éxito dinámicos reportados por donaciones-service
        try {
            List<CasoExitoDto> casosExito = donacionService.obtenerCasosExito();
            if (casosExito != null) {
                for (CasoExitoDto caso : casosExito) {
                    puntos.add(new PuntoEntregaDto(
                            caso.idDonacion(),
                            caso.nombreEntidad() != null ? caso.nombreEntidad() : "Comedor Comunitario",
                            "Entidad Beneficiaria",
                            caso.tituloDonacion() != null ? caso.tituloDonacion() : "Donación entregada",
                            caso.descripcionDonacion() != null ? caso.descripcionDonacion() : "Entrega verificada por auditoría de recepción.",
                            caso.direccionEntidad() != null ? caso.direccionEntidad() : "CABA",
                            caso.latitud() != null ? caso.latitud() : -34.6175,
                            caso.longitud() != null ? caso.longitud() : -58.3675,
                            caso.fotoEntrega() != null && !caso.fotoEntrega().isBlank()
                                    ? caso.fotoEntrega()
                                    : "https://images.unsplash.com/photo-1593113598332-cd288d649433?auto=format&fit=crop&w=600&q=80",
                            caso.fechaEntrega() != null ? caso.fechaEntrega().toString() : "Entrega verificada",
                            "ENTREGADA"
                    ));
                }
            }
        } catch (Exception e) {
            log.warn("[PuntosEntregaApi] No se pudieron cargar casos dinámicos: {}", e.getMessage());
        }

        // 2. Solo mostrar el depósito central además de las entregas reales confirmadas
        puntos.add(new PuntoEntregaDto(
                5L,
                "Depósito Central DonaTrack",
                "Centro Logístico de Acopio y Despacho",
                "Base Logística Central",
                "Centro de pesaje, cubicaje y asignación de donaciones.",
                "Av. Medrano 951, CABA",
                -34.5985,
                -58.4201,
                "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=600&q=80",
                "Operativo Continuo",
                "BASE_CENTRAL"
        ));

        return ResponseEntity.ok(puntos);
    }
}

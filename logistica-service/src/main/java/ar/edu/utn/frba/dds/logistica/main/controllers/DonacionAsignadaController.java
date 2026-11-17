package ar.edu.utn.frba.dds.logistica.main.controllers;

import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.DonacionAsignadaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.PesajeDonacionRequest;
import ar.edu.utn.frba.dds.logistica.main.services.RecepcionDonacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/logistica-service/recepcion")
public class DonacionAsignadaController {
    private final RecepcionDonacionService service;

    public DonacionAsignadaController(RecepcionDonacionService service) {
        this.service = service;
    }

    @PostMapping("/asignacion")
    public ResponseEntity<String> recibirAsignacion(@RequestBody DonacionAsignadaRequest request) {
        log.info("[Logistica-Controller] POST /logistica-service/recepcion/asignacion recibido: idDonacion={}, subcat={}, cantidad={}, entidad={}, dir={}",
                request.getDonacionId(), request.getSubcategoria(), request.getCantidad(), request.getRazonSocialEntidad(), request.getDireccionEntidad());
        service.procesarAsignacion(request);
        log.info("[Logistica-Controller] Asignacion procesada con exito para idDonacion={}", request.getDonacionId());
        return ResponseEntity.ok("Donacion recibida y lista para planificar");
    }

    @PostMapping("/pesaje-donacion/{id}")
    public DonacionResponse cargarPesajeDonacion(@RequestBody PesajeDonacionRequest request, @PathVariable Long id){
        log.info("[Logistica-Controller] POST /logistica-service/recepcion/pesaje-donacion/{} recibido: peso={}, volumen={}", id, request.peso(), request.volumen());
        DonacionResponse response = service.cargarPesaje(request, id);
        log.info("[Logistica-Controller] Pesaje registrado con exito para id={}", id);
        return response;
    }
}

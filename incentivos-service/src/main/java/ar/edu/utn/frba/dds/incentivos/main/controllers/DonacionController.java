package ar.edu.utn.frba.dds.incentivos.main.controllers;

import ar.edu.utn.frba.dds.incentivos.main.dtos.ActualizarDonacionRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.CrearDonacionRequest;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.services.DonacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
@RequestMapping("/donacion")
public class DonacionController {
    private final DonacionService donacionService;

    public DonacionController(DonacionService donacionService) {
        this.donacionService = donacionService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('DONACIONES_SERVICE')")
    public Donacion crearDonacion(@RequestBody CrearDonacionRequest donacionReq) {
        log.info("[Incentivos-Controller] POST /donacion recibido: idDonacion={}, idDonante={}, cantidad={}, subcat={}, cat={}",
                donacionReq.idDonacion(), donacionReq.idDonante(), donacionReq.CantidadDeBien(),
                donacionReq.subcategoria(), donacionReq.categoria());
        Donacion resultado = this.donacionService.agregarDonacion(donacionReq);
        log.info("[Incentivos-Controller] POST /donacion procesado con exito para idDonacion={}", donacionReq.idDonacion());
        return resultado;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DONACIONES_SERVICE') or hasRole('LOGISTICA_SERVICE')")
    public Donacion marcarDonacionComoRecibida(@PathVariable("id") Long idDonacion, @RequestParam("nombreDeLaEntidad") String entidadId) {
        log.info("[Incentivos-Controller] PUT /donacion/{} recibido: entidad={}", idDonacion, entidadId);
        Donacion resultado = this.donacionService.marcarDonacionComoRecibida(idDonacion, entidadId);
        log.info("[Incentivos-Controller] PUT /donacion/{} procesado con exito", idDonacion);
        return resultado;
    }

}

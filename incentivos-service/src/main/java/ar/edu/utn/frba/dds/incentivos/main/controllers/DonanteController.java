package ar.edu.utn.frba.dds.incentivos.main.controllers;

import ar.edu.utn.frba.dds.incentivos.main.dtos.CrearDonanteRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.DonanteResponse;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequestMapping("/donante")
public class DonanteController {
    private final DonanteService donanteService;

    public DonanteController(DonanteService donanteService) {
        this.donanteService = donanteService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('DONACIONES_SERVICE')")
    public DonanteResponse crearDonante(@RequestBody CrearDonanteRequest donante) {
        log.info("[Incentivos-Controller] POST /donante recibido: idDonante={}, contactosCount={}",
                donante.id(), donante.contactos() != null ? donante.contactos().size() : 0);
        DonanteResponse response = donanteService.agregarDonante(donante);
        log.info("[Incentivos-Controller] POST /donante procesado con exito para idDonante={}", donante.id());
        return response;
    }
}

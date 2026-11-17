package ar.edu.utn.frba.dds.donaciones.main.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String decirHola() {
        return "Hola desde el servicio de Donaciones.";
    }
}

package ar.edu.utn.frba.dds.logistica.main.controllers;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.RoutingResult;
import ar.edu.utn.frba.dds.logistica.main.exception.EstructuraInesperadaException;
import ar.edu.utn.frba.dds.logistica.main.services.PlanificadorDeRutasService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.MessageDigest;

@Slf4j
@RestController
@RequestMapping
public class PlanificadorRutasController {
    private final PlanificadorDeRutasService planificadorDeRutasService;
    private final String clientSecret;
    private final ObjectMapper objectMapper;

    public PlanificadorRutasController(PlanificadorDeRutasService planificadorDeRutasService, @Value("${rutasApi.clientSecret}") String clientSecret, ObjectMapper objectMapper) {
        this.planificadorDeRutasService = planificadorDeRutasService;
        this.clientSecret = clientSecret;
        this.objectMapper = objectMapper;
    }


    @PostMapping
    public ResponseEntity<String> confirmarRutas(@RequestBody String resultString, @RequestHeader("X-Signature") String receivedSignature) {
        String calculatedSignature = "sha256=" + Codificador.calculateHmac256(resultString, clientSecret);

        if (!MessageDigest.isEqual(calculatedSignature.getBytes(), receivedSignature.getBytes())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Firma inválida.");
        }

        try {
            RoutingResult result = objectMapper.readValue(resultString, RoutingResult.class);

            System.out.println("Resultado de rutas recibido: " + resultString);
            planificadorDeRutasService.procesarRutas(result);

            return ResponseEntity.ok("Recibido y procesando estructura de éxito.");

        } catch (JsonProcessingException e) {
            throw new EstructuraInesperadaException("El servidor envió una estructura inesperada (posiblemente error). Payload crudo: \n" + resultString, e);
        }

    }
}


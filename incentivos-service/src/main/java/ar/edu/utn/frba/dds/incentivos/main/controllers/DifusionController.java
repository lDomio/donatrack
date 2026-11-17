package ar.edu.utn.frba.dds.incentivos.main.controllers;

import ar.edu.utn.frba.dds.incentivos.main.dtos.InsigniaObtenidaRequest;
import ar.edu.utn.frba.dds.incentivos.main.services.DifusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incentivos-service/difusion")
public class DifusionController {

  @Autowired
  private DifusionService difusionService;

  @PostMapping("/insignia")
  public void difundirInsignia(@RequestBody InsigniaObtenidaRequest insigniaObtenida) {
    difusionService.difundirMensajeObtencionInsignia(insigniaObtenida);
  }
}

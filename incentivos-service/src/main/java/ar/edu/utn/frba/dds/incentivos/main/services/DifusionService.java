package ar.edu.utn.frba.dds.incentivos.main.services;

import ar.edu.utn.frba.dds.incentivos.main.dtos.InsigniaObtenidaRequest;

public interface DifusionService {
  public void difundirMensajeObtencionInsignia(InsigniaObtenidaRequest insigniaObtenida);
}

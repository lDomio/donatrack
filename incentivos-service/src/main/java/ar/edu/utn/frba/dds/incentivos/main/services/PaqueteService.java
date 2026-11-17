package ar.edu.utn.frba.dds.incentivos.main.services;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;

public interface PaqueteService {
  void enviarPaquetes();
  void guardarPaquete(Paquete paquete);
}

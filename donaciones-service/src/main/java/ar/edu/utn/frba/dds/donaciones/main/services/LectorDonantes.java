package ar.edu.utn.frba.dds.donaciones.main.services;

import ar.edu.utn.frba.dds.donaciones.main.dtos.DonanteCsv.RegistroDonanteDto;
import java.io.InputStream;
import java.util.List;

public interface LectorDonantes {
  /**
   * Método que define el contrato para cualquier lector de donantes
   * 
   * @param inputStream Flujo de entrada que contiene los datos de los donantes
   * @return Una lista de donantes leídos desde el inputStream
   */
  List<RegistroDonanteDto> leerDonantes(InputStream inputStream);
}

package ar.edu.utn.frba.dds.donaciones.main.infrastructure;

import java.time.LocalDate;

public interface LogisticaClient {
  void donacionAsignada(Long idDonacion, Long idDonante, String subcategoriaDonacion, Double cantidad, LocalDate fechaVencimiento, String razonSocialEntidad, Long idEntidad, String direccionEntidad, Double latitud, Double longitud);
}

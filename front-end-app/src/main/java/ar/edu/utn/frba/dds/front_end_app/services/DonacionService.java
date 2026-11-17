package ar.edu.utn.frba.dds.front_end_app.services;

import ar.edu.utn.frba.dds.front_end_app.dto.CasoExitoDto;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonacionApiService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DonacionService {
  private final DonacionApiService donacionApiService;

  public DonacionService(DonacionApiService donacionApiService) {
    this.donacionApiService = donacionApiService;
  }

  public List<DonacionResponseEntidad> getDonacionesByNecesidad(Long idNecesidad) {
    return donacionApiService.getDonacionesByNecesidad(idNecesidad);
  }

  public void confirmarDonacion(Long id, String motivo, String autor) {
    confirmarDonacion(id, motivo, autor, null);
  }

  public void confirmarDonacion(Long id, String motivo, String autor, String fotoEntrega) {
    CambiarEstadoRequest request = new CambiarEstadoRequest(motivo, LocalDate.now(), "ENTREGADA", autor, fotoEntrega);
    donacionApiService.confirmarDonacion(id, request);
  }

  public List<CasoExitoDto> obtenerCasosExito() {
    return donacionApiService.obtenerCasosExito();
  }

  public DonacionResponse obtenerDonacion(Long idDonacion){
    return donacionApiService.obtenerDonacion(idDonacion);
  }
}

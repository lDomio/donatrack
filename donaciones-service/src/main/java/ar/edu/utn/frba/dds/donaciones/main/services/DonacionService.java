package ar.edu.utn.frba.dds.donaciones.main.services;

import ar.edu.utn.frba.dds.donaciones.main.dtos.AdministradorRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CasoExitoResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.EntidadesAyudadasResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.sugerenciaMatch.SugerenciaMatchResponse;
import ar.edu.utn.frba.dds.donaciones.main.events.dtos.InicioRutaEventDto;
import ar.edu.utn.frba.dds.donaciones.main.events.dtos.InicioRutaEventDtoSalida;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import java.time.YearMonth;
import java.util.List;

public interface DonacionService {

  public List<DonacionResponse> obtenerTodas();
  public List<DonacionResponse> obtenerDonaciones(Long id);
  public EntidadesAyudadasResponse contarEntidadesAyudadasEsteMes(Long idDonante);
  public List<DonacionResponse> obtenerPorIdNecesidad(Long idNecesidad);
  public List<DonacionResponse> crearDonacion(CrearDonacionRequest request);
  public void eliminarDonacion(Long id);
  public DonacionResponse actualizarEstado(Long id, CambiarEstadoRequest request);
  public List<SugerenciaMatchResponse> sugerenciaDeMatcheoDeDonacion();
  public void realizarMatcheoDonaciones();
  public void seleccionarNecesidad(Long idMatcheo,Long idNecesidadRecibidora, AdministradorRequest request);
  public void notificarDonanteEntidadBeneficiaria(List<InicioRutaEventDtoSalida> eventoInicioDeRuta);
  public MedioDeContacto recuperarMedioContactoDonante(Long id);
  public MedioDeContacto recuperarMedioContactoEntidadBeneficiaria(String razonSocial);
  public DonacionResponse mapearDonacionResponse(Donacion donacion);
  public List<CasoExitoResponse> obtenerCasosExito();
  DonacionResponse obtenerDonacionPorId(Long idDonacion);
}

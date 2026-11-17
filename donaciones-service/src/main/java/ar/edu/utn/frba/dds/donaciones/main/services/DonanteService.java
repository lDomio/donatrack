package ar.edu.utn.frba.dds.donaciones.main.services;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteCreateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;

import java.util.List;

public interface DonanteService {

  public List<DonanteResponse> obtenerTodos();
  public List<DonanteResponse> obtenerTodosIncluyendoInactivos(String buscar, String tipo, String estado);
  public Donante obtenerDonante(Long id);
  public Donante obtenerDonanteActivo(Long id);
  public DonanteResponse buscarPorId(Long id);
  public DonanteResponse buscarPorIdUsuarioAuth(Long idUsuarioAuth);
  public Boolean validarUsuarioDeDonante(Long idDonante, Long idUsuarioAuth);
  public Boolean validarDonacionRequestDeDonante(CrearDonacionRequest donacionRequest, Long idUsuarioAuth);
  public DonanteResponse agregarDonante(DonanteCreateRequest donanteCreateRequest);
  public void eliminarDonante(Long id);
  public void darDeBajaDonante(Long id);
  public void darDeAltaDonante(Long id);
  public void verificarInactividadDonantes();
  public MedioDeContacto buscarMedioDeContactoPorId(Long usuariosId);
  public DonanteResponse agregarRepresentante(Long idDonante, RepresentanteDTO representanteDTO);
  public DonanteResponse editarRepresentante(Long idDonante, Long idRepresentante, RepresentanteDTO representanteDTO);
  public DonanteResponse eliminarRepresentante(Long idDonante, Long idRepresentante);
}

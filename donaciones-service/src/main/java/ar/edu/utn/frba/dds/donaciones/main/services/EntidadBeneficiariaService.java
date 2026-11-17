package ar.edu.utn.frba.dds.donaciones.main.services;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.*;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;

import java.util.List;

public interface EntidadBeneficiariaService {
  public List<EntidadBeneficiariaResponse> obtenerTodas();
  public List<EntidadBeneficiariaResponse> buscarConFiltros(String busqueda, String nombre, String ubicacion, String tipo);
  public EntidadBeneficiariaResponse findById(Long id);
  public EntidadBeneficiariaResponse buscarPorIdUsuarioAuth(Long idUsuarioAuth);
  public Boolean validarNecesidadDeUsuario(Long idNecesidad, Long idUsuarioAuth);
  public Boolean validarConfirmacionDeDonacion(CambiarEstadoRequest estadoRequest, Long idDonacion, Long idUsuarioAuth);
  public Boolean validarEntidadDeUsuario(Long idEntidad, Long idUsuarioAuth);
  public List<NecesidadDto> findNecesidadesByIdEntidad(Long idEntidad);
  public List<DonacionResponse> findDonacionesByIdEntidad(Long idEntidad);
  public NecesidadDto findNecesidadById(Long idNecesidad);
  public EntidadBeneficiariaResponse crear(EntidadBeneficiariaRequest request);
  public EntidadBeneficiariaResponse actualizar(Long id, EntidadBeneficiariaRequest request);
  public void eliminar(Long id);
  public void agregarNecesidad(Long id, NecesidadRequest request);
  public void eliminarNecesidad(Long id, Long idNecesidad);
  public List<Necesidad> findAllNecesidades();
  public Necesidad findByNecesidadId(Long idNecesidad);
  public EntidadBeneficiaria findByRazonSocial(String razonSocial);
  public void guardarEntidad(EntidadBeneficiaria entidadBeneficiaria);
}

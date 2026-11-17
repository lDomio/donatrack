package ar.edu.utn.frba.dds.donaciones.main.infrastructure;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;

import java.time.LocalDate;

public interface IncentivosClient {
    void agregarDonacion(Long idDonacion,Long idDonante, Double cantidadBienes, String nombreSubcategoria, String nombreCategoria, LocalDate fecha, Long idEntidadBeneficiaria);
    void actualizarDonacion(Long idDonacion, String razonSocialEntidadBeneficiaria);
    void agregarDonante(DonanteResponse donante, Long idUsuarioAuth);
}

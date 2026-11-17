package ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria;

import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaSimpleResponse;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public record NecesidadDto(
    Long id,
    SubcategoriaResponse subcategoria,
    String tipoNecesidad,
    Double cantidad,
    Double cantidadDonada,
    Period periodoRecurrencia,
    LocalDate inicioRecurrencia,
    LocalDate proximoReinicio,
    String tiempoParaProximoReinicio,
    List<DonacionResponse> donacionesAsignadas,
    List<PeriodoDto> historialDePeriodos,
    String razonSocialEntidad,
    String descripcion,
    String algoritmoRecomendador,
    Integer rankingSemantica,
    Double puntajeSemantica,
    Integer rankingPrioridad,
    Double puntajePrioridad,
    Integer donacionesTrimestreEntidad
) {
  public NecesidadDto(
      Long id,
      SubcategoriaResponse subcategoria,
      String tipoNecesidad,
      Double cantidad,
      Double cantidadDonada,
      Period periodoRecurrencia,
      LocalDate inicioRecurrencia,
      LocalDate proximoReinicio,
      String tiempoParaProximoReinicio,
      List<DonacionResponse> donacionesAsignadas,
      List<PeriodoDto> historialDePeriodos
  ) {
    this(
        id,
        subcategoria,
        tipoNecesidad,
        cantidad,
        cantidadDonada,
        periodoRecurrencia,
        inicioRecurrencia,
        proximoReinicio,
        tiempoParaProximoReinicio,
        donacionesAsignadas,
        historialDePeriodos,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );
  }
}

package ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria;

import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NecesidadDTO(Long id,
                           SubcategoriaResponse subcategoria,
                           String tipoNecesidad,
                           Double cantidad,
                           Double cantidadDonada,
                           Period periodoRecurrencia,
                           LocalDate inicioRecurrencia,
                           LocalDate proximoReinicio,
                           String tiempoParaProximoReinicio,
                           List<DonacionResponseEntidad> donacionesAsignadas,
                           List<PeriodoDTO> historialDePeriodos,
                           String razonSocialEntidad,
                           String descripcion,
                           String algoritmoRecomendador,
                           Integer rankingSemantica,
                           Double puntajeSemantica,
                           Integer rankingPrioridad,
                           Double puntajePrioridad,
                           Integer donacionesTrimestreEntidad) {
}

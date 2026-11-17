package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper;

import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.DESCRIPCION_PENDIENTE;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.aInt;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.componerUbicacion;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.esCritica;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.porcentaje;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.MetricaVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.NecesidadResumenDto;
import ar.edu.utn.frba.dds.front_end_app.dto.NecesidadResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Unico lugar donde se derivan metricas, progreso, criticidad y fallbacks de
 * {@code EntidadBeneficiariaResponse}. Clase pura (sin Spring) para poder
 * testearla sin levantar contexto.
 *
 * Fallbacks: ubicacion {@code null} cuando el backend no trae direccion
 * (el template omite la linea), descripcion "Descripcion pendiente",
 * imagen {@code null}, impacto 0 con icono por tipo.
 * Las reglas de criticidad y ubicacion viven en {@link EntidadMapperSupport},
 * compartidas con el mapper del detalle.
 */
public final class EntidadVistaMapper {

  private static final String ICONO_DEFAULT = "fa-hand-holding-heart";

  private static final Map<String, String> ICONOS_POR_TIPO = Map.of(
      "COMEDOR", "fa-users",
      "EDUCACION", "fa-graduation-cap",
      "SALUD", "fa-heart-pulse",
      "ONG", "fa-hand-holding-heart",
      "REFUGIO", "fa-bed");

  private EntidadVistaMapper() {
  }

  public static List<EntidadVistaDto> aVista(List<EntidadBeneficiariaResponse> responses) {
    return responses.stream().map(EntidadVistaMapper::aVista).collect(Collectors.toList());
  }

  public static EntidadVistaDto aVista(EntidadBeneficiariaResponse response) {
    List<NecesidadResponse> necesidades = response.necesidades() != null ? response.necesidades() : List.of();

    List<MetricaVistaDto> metricas = necesidades.stream()
        .map(EntidadVistaMapper::aMetrica)
        .collect(Collectors.toList());

    boolean algunaCritica = metricas.stream().anyMatch(MetricaVistaDto::critica);

    List<NecesidadResumenDto> resumen = metricas.stream()
        .sorted(Comparator.comparing(MetricaVistaDto::critica).reversed())
        .map(m -> new NecesidadResumenDto(
            m.nombre(), m.cantidadRequerida(), m.cantidadDonada(), m.porcentaje(),
            m.critica(), m.critica() ? "alerta" : "",
            m.subcategoriaId(), m.unidadSugerida(), m.faltante()))
        .collect(Collectors.toList());

    return new EntidadVistaDto(
        response.id(),
        response.razonSocial(),
        response.tipo(),
        componerUbicacion(response.direccion()),
        DESCRIPCION_PENDIENTE,
        null,
        metricas,
        progresoPonderado(necesidades),
        algunaCritica ? "alerta" : "",
        0,
        iconoPara(response.tipo()),
        resumen);
  }

  private static MetricaVistaDto aMetrica(NecesidadResponse necesidad) {
    int requerida = aInt(necesidad.cantidad());
    int donada = aInt(necesidad.cantidadDonada());
    String nombre = necesidad.subcategoria() != null && necesidad.subcategoria().nombre() != null
        ? necesidad.subcategoria().nombre() : "Sin nombre";
    Long subId = necesidad.subcategoria() != null ? necesidad.subcategoria().id() : null;
    String unidad = necesidad.subcategoria() != null ? necesidad.subcategoria().unidadDeMedida() : null;
    return new MetricaVistaDto(
        nombre,
        requerida,
        donada,
        porcentaje(requerida, donada),
        esCritica(requerida, donada, necesidad.tipoNecesidad(), necesidad.periodoRecurrencia()),
        subId,
        unidad,
        Math.max(0, requerida - donada));
  }

  private static int progresoPonderado(List<NecesidadResponse> necesidades) {
    int totalRequerida = necesidades.stream().mapToInt(n -> aInt(n.cantidad())).sum();
    if (totalRequerida <= 0) {
      return 0;
    }
    int totalDonada = necesidades.stream().mapToInt(n -> aInt(n.cantidadDonada())).sum();
    return (int) Math.round(totalDonada * 100.0 / totalRequerida);
  }

  private static String iconoPara(String tipo) {
    if (tipo == null) {
      return ICONO_DEFAULT;
    }
    return ICONOS_POR_TIPO.getOrDefault(tipo.toUpperCase(), ICONO_DEFAULT);
  }

}

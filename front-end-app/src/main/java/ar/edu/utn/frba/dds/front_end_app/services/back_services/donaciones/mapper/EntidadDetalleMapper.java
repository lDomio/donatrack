package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaNecesidadDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadDetalleDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadDetalleVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.InsumoNecesidadDto;
import ar.edu.utn.frba.dds.front_end_app.dto.NecesidadResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.DESCRIPCION_PENDIENTE;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.aInt;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.componerUbicacion;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.esCritica;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.esExtraordinaria;
import static ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadMapperSupport.porcentaje;

/**
 * Mapper del detalle de entidad: cabecera + categorias de necesidades agrupadas
 * por tipo (extraordinarias primero, recurrentes despues; grupos vacios se omiten).
 * Clase pura (sin Spring) para poder testearla sin levantar contexto.
 */
public final class EntidadDetalleMapper {

  private EntidadDetalleMapper() {
  }

  public static EntidadDetalleVistaDto aVista(EntidadBeneficiariaResponse response) {
    List<NecesidadResponse> necesidades = response.necesidades() != null ? response.necesidades() : List.of();

    EntidadDetalleDto entidad = new EntidadDetalleDto(
        response.id(),
        response.razonSocial(),
        response.tipo(),
        componerUbicacion(response.direccion()),
        DESCRIPCION_PENDIENTE,
        null,
        false,
        List.of());

    return new EntidadDetalleVistaDto(entidad, aCategorias(necesidades));
  }

  static List<CategoriaNecesidadDto> aCategorias(List<NecesidadResponse> necesidades) {
    List<NecesidadResponse> extraordinarias = necesidades.stream()
        .filter(n -> esExtraordinaria(n.tipoNecesidad(), n.periodoRecurrencia()))
        .collect(Collectors.toList());
    List<NecesidadResponse> recurrentes = necesidades.stream()
        .filter(n -> !esExtraordinaria(n.tipoNecesidad(), n.periodoRecurrencia()))
        .collect(Collectors.toList());

    List<CategoriaNecesidadDto> categorias = new ArrayList<>();
    if (!extraordinarias.isEmpty()) {
      categorias.add(new CategoriaNecesidadDto(
          "Necesidades extraordinarias",
          "Pedidos puntuales para situaciones o eventos especificos.",
          "fa-star", "orange", null,
          extraordinarias.stream().map(EntidadDetalleMapper::aInsumo).collect(Collectors.toList())));
    }
    if (!recurrentes.isEmpty()) {
      categorias.add(new CategoriaNecesidadDto(
          "Necesidades recurrentes",
          "Se renuevan en cada periodo: hay que reponerlas todo el ano.",
          "fa-arrows-rotate", "teal",
          frecuenciaDelGrupo(recurrentes),
          recurrentes.stream().map(EntidadDetalleMapper::aInsumo).collect(Collectors.toList())));
    }
    return categorias;
  }

  private static InsumoNecesidadDto aInsumo(NecesidadResponse necesidad) {
    double requerida = necesidad.cantidad() != null ? necesidad.cantidad() : 0.0;
    double donada = necesidad.cantidadDonada() != null ? necesidad.cantidadDonada() : 0.0;
    boolean urgente = esCritica(aInt(necesidad.cantidad()), aInt(necesidad.cantidadDonada()),
        necesidad.tipoNecesidad(), necesidad.periodoRecurrencia());
    boolean completado = donada >= requerida;
    String nombre = necesidad.subcategoria() != null && necesidad.subcategoria().nombre() != null
        ? necesidad.subcategoria().nombre() : "Sin nombre";
    Long subId = necesidad.subcategoria() != null ? necesidad.subcategoria().id() : null;
    String unidad = necesidad.subcategoria() != null ? necesidad.subcategoria().unidadDeMedida() : null;
    int faltante = Math.max(0, (int) Math.round(requerida - donada));
    return new InsumoNecesidadDto(
        nombre,
        requerida,
        donada,
        porcentaje(aInt(necesidad.cantidad()), aInt(necesidad.cantidadDonada())),
        urgente,
        completado,
        completado ? "green" : (urgente ? "red" : "teal"),
        subId,
        unidad,
        faltante);
  }

  /**
   * Etiqueta del grupo si todos los periodos presentes coinciden; {@code null} si hay
   * mezcla, valores desconocidos o ningun periodo (el template omite la etiqueta).
   */
  static String frecuenciaDelGrupo(List<NecesidadResponse> necesidades) {
    List<String> etiquetas = necesidades.stream()
        .map(NecesidadResponse::periodoRecurrencia)
        .map(EntidadDetalleMapper::etiquetaFrecuencia)
        .filter(e -> e != null)
        .distinct()
        .collect(Collectors.toList());
    return etiquetas.size() == 1 ? etiquetas.get(0) : null;
  }

  private static String etiquetaFrecuencia(String periodo) {
    if (periodo == null) {
      return null;
    }
    return switch (periodo.trim()) {
      case "P1M" -> "Mensual";
      case "P1Y" -> "Anual";
      default -> null;
    };
  }
}

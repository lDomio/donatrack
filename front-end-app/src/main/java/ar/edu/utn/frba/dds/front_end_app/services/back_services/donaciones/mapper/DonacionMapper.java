package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.BienResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.DonacionResponseDonante;
import ar.edu.utn.frba.dds.front_end_app.dto.DonacionVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.TipoDeEstado;

import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Unico lugar donde se derivan titulo, labels de estado y fallbacks de {@code DonacionResponse}.
 * Clase pura (sin Spring) para poder testearla sin levantar contexto.
 */
public final class DonacionMapper {

  private static final String SIN_CATEGORIA = "Sin categoria";

  private static final Map<TipoDeEstado, String> LABELS_ESTADO = new EnumMap<>(TipoDeEstado.class);

  static {
    LABELS_ESTADO.put(TipoDeEstado.DEPOSITO, "En deposito");
    LABELS_ESTADO.put(TipoDeEstado.ASIGNACION_REALIZADA, "Asignada");
    LABELS_ESTADO.put(TipoDeEstado.LISTA_PARA_ENTREGAR, "Lista para entregar");
    LABELS_ESTADO.put(TipoDeEstado.EN_TRASLADO, "En transporte");
    LABELS_ESTADO.put(TipoDeEstado.ENTREGADA, "Entregada");
    LABELS_ESTADO.put(TipoDeEstado.ENTREGA_FALLIDA, "Entrega fallida");
    LABELS_ESTADO.put(TipoDeEstado.VENCIDO, "Vencida");
  }

  /** Orden fijo para poblar el filtro de estados, independiente de los datos cargados. */
  public static final List<String> ESTADOS_DISPONIBLES = List.copyOf(LABELS_ESTADO.values());

  private DonacionMapper() {
  }

  public static List<DonacionVistaDto> aVista(List<DonacionResponseDonante> responses) {
    return responses.stream().map(DonacionMapper::aVista).collect(Collectors.toList());
  }

  public static DonacionVistaDto aVista(DonacionResponseDonante response) {
    SubcategoriaResponse subcategoria = response.subcategoria();
    TipoDeEstado estado = response.estado();

    return new DonacionVistaDto(
        response.id(),
        titulo(response),
        subcategoria != null ? subcategoria.categoriaNombre() : SIN_CATEGORIA,
        subcategoria != null ? subcategoria.nombre() : SIN_CATEGORIA,
        response.descripcionGeneral(),
        LABELS_ESTADO.get(estado),
        claseEstado(estado),
        response.fotoEntrega(),
        estado == TipoDeEstado.EN_TRASLADO
    );
  }

  private static String titulo(DonacionResponseDonante response) {
    List<BienResponse> bienes = response.bienes();
    if (bienes != null && !bienes.isEmpty() && bienes.get(0).nombre() != null) {
      BienResponse primero = bienes.get(0);
      String cantidad = formatearCantidad(primero.cantidad());
      return cantidad.isEmpty() ? primero.nombre() : cantidad + " " + primero.nombre();
    }
    if (response.descripcionGeneral() != null && !response.descripcionGeneral().isBlank()) {
      return response.descripcionGeneral();
    }
    return "Donacion #" + response.id();
  }

  private static String formatearCantidad(Double cantidad) {
    if (cantidad == null) {
      return "";
    }
    if (cantidad == Math.floor(cantidad)) {
      return String.valueOf(cantidad.intValue());
    }
    return String.valueOf(cantidad);
  }

  private static String claseEstado(TipoDeEstado estado) {
    return switch (estado) {
      case EN_TRASLADO, LISTA_PARA_ENTREGAR, ASIGNACION_REALIZADA -> "activo";
      case ENTREGADA -> "exito";
      case DEPOSITO, ENTREGA_FALLIDA, VENCIDO -> "";
    };
  }


}

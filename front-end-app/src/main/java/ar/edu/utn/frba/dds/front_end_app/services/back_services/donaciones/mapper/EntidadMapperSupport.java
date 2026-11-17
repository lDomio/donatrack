package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.DireccionDto;

/**
 * Helpers compartidos por los mappers de la vidriera (grilla y detalle).
 * Clase pura (sin Spring). Uso interno del paquete.
 */
final class EntidadMapperSupport {

  static final double UMBRAL_CRITICO = 30.0;
  static final String DESCRIPCION_PENDIENTE = "Descripcion pendiente";

  private EntidadMapperSupport() {
  }

  static int aInt(Double valor) {
    return valor == null ? 0 : (int) Math.round(valor);
  }

  static int porcentaje(int requerida, int donada) {
    if (requerida <= 0) {
      return 0;
    }
    return (int) Math.round(donada * 100.0 / requerida);
  }

  static boolean esCritica(int requerida, int donada, String tipoNecesidad, String periodoRecurrencia) {
    if (esExtraordinaria(tipoNecesidad, periodoRecurrencia)) {
      return true;
    }
    return requerida > 0 && donada * 100.0 / requerida < UMBRAL_CRITICO;
  }

  /**
   * El back serializa {@code tipoNecesidad} como {@code Clase@hash}, nunca como enum,
   * asi que se infiere: sin periodo de recurrencia es extraordinaria (la recurrente
   * exige periodo en el dominio). Si el back expone el enum, el primer chequeo lo toma.
   */
  static boolean esExtraordinaria(String tipoNecesidad, String periodoRecurrencia) {
    if ("EXTRAORDINARIA".equalsIgnoreCase(tipoNecesidad)) {
      return true;
    }
    return periodoRecurrencia == null || periodoRecurrencia.isBlank();
  }

  static String componerUbicacion(DireccionDto direccion) {
    if (direccion == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    if (direccion.calle() != null && !direccion.calle().isBlank()) {
      sb.append(direccion.calle().trim());
    }
    if (direccion.altura() != null) {
      if (!sb.isEmpty()) {
        sb.append(' ');
      }
      sb.append(direccion.altura());
    }
    if (direccion.ciudad() != null && !direccion.ciudad().isBlank()) {
      if (!sb.isEmpty()) {
        sb.append(", ");
      }
      sb.append(direccion.ciudad().trim());
    }
    return sb.isEmpty() ? null : sb.toString();
  }
}

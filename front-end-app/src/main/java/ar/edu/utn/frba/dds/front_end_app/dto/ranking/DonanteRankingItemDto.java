package ar.edu.utn.frba.dds.front_end_app.dto.ranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DonanteRankingItemDto(
    Integer puesto,
    Long idDonante,
    Long idDonanteDonacionesService,
    String nombreUsuario,
    Integer cantidadDonaciones,
    Long misionesCompletadas
) {
    public String getNombreVisual() {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return "Donante #" + (idDonante != null ? idDonante : "");
        }
        return Character.toUpperCase(nombreUsuario.charAt(0)) + nombreUsuario.substring(1);
    }

    public String getIniciales() {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return "D" + (puesto != null ? puesto : "");
        }
        String[] partes = nombreUsuario.trim().split("\\s+");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        }
        return nombreUsuario.length() >= 2
                ? nombreUsuario.substring(0, 2).toUpperCase()
                : nombreUsuario.toUpperCase();
    }

    public String getClaseMedalla() {
        if (puesto == null) return "";
        return switch (puesto) {
            case 1 -> "oro";
            case 2 -> "plata";
            case 3 -> "bronce";
            default -> "";
        };
    }

    public String getMetaInfo() {
        if (idDonanteDonacionesService != null) {
            return "ID Donante #" + idDonanteDonacionesService;
        }
        if (idDonante != null) {
            return "ID #" + idDonante;
        }
        return "Donante registrado";
    }
}

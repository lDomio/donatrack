package ar.edu.utn.frba.dds.front_end_app.dto;

public record PinMapaDto(
        Long id,
        String nombre,
        String clasePosicion,
        String etiquetaEstado,
        String tiempoEstimado,
        String enlace,
        boolean esCamionEnMovimiento
) {
}

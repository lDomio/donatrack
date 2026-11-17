package ar.edu.utn.frba.dds.front_end_app.dto;

public record DonacionVistaDto(
        Long id,
        String titulo,
        String categoria,
        String subcategoria,
        String descripcion,
        String estado,
        String claseEstado,
        String imagenUrl,
        boolean destacado
) {
}

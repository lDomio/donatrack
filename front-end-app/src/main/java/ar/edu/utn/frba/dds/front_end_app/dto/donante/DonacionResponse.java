package ar.edu.utn.frba.dds.front_end_app.dto.donante;

import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import java.time.LocalDate;
import java.util.List;


public record DonacionResponse(
    Long id,
    List<BienResponse> bienes,
    Double cantidadDonada,
    String descripcionGeneral,
    TipoDeEstado estado,
    LocalDate ultimaActualizacion,
    SubcategoriaResponse subcategoria,
    Long NecesidadAsignadaId,
    Long PeriodoAsignadoId,
    String nombreEntidad
) {
}

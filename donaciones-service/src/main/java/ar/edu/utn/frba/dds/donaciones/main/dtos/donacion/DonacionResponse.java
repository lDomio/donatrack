package ar.edu.utn.frba.dds.donaciones.main.dtos.donacion;

import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;

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
    String fotoEntrega,
    String nombreEntidad
) {
    public DonacionResponse(
        Long id,
        List<BienResponse> bienes,
        Double cantidadDonada,
        String descripcionGeneral,
        TipoDeEstado estado,
        LocalDate ultimaActualizacion,
        SubcategoriaResponse subcategoria,
        Long NecesidadAsignadaId,
        Long PeriodoAsignadoId,
        String nombreEntidad) {
        this(id, bienes, cantidadDonada, descripcionGeneral, estado, ultimaActualizacion, subcategoria, NecesidadAsignadaId, PeriodoAsignadoId, null, nombreEntidad);
    }
}

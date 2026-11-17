package ar.edu.utn.frba.dds.front_end_app.dto;

import java.util.List;

public record IncentivosVistaDto(
        List<CategoriaDonanteDto> categorias,
        String categoriaActual,
        String categoriaSiguiente,
        int misionesFaltantesCategoria,
        List<String> filtrosCategoriaMision,
        List<MisionVistaDto> misiones,
        List<InsigniaVistaDto> insignias,
        int insigniasObtenidas,
        int insigniasTotales
) {
}

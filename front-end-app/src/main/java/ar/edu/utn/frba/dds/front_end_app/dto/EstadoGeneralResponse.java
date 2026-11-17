package ar.edu.utn.frba.dds.front_end_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EstadoGeneralResponse(
        CategoriaDonanteResponse categoriaActual,
        CategoriaDonanteResponse categoriaSiguiente,
        MisionEstadoResponse misionActual,
        ProgresoMisionResponse progresoMisionActual,
        Integer misionesCompletadasCategoria,
        Integer misionesRequeridasCategoria
) {
}

package ar.edu.utn.frba.dds.incentivos.main.dtos;

public record EstadoGeneralResponse(
    CategoriaDonanteDto categoriaActual,
    CategoriaDonanteDto categoriaSiguiente,
    MisionDto misionActual,
    ProgresoMisionDto progresoMisionActual,
    Integer misionesCompletadasCategoria,
    Integer misionesRequeridasCategoria
) {}

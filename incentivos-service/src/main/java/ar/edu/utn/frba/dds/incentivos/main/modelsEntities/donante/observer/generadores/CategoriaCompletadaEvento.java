package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;

public record CategoriaCompletadaEvento(
    Donante donante,
    CategoriaCompletada categoriaAnterior,
    CategoriaDonante categoriaActual
) {
}

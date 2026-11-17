package ar.edu.utn.frba.dds.incentivos.main.dtos;

import java.time.LocalDate;

public record CategoriaCompletadaDto(
    CategoriaDonanteDto categoria,
    LocalDate fechaDeComplecion
) {}

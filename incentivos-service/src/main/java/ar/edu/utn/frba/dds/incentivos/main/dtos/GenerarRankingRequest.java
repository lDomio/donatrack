package ar.edu.utn.frba.dds.incentivos.main.dtos;

public record GenerarRankingRequest(Integer mes, Integer anio) {
    public GenerarRankingRequest {
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }
        if (anio == null || anio < 2000) {
            throw new IllegalArgumentException("El año debe ser mayor o igual a 2000");
        }
    }
}

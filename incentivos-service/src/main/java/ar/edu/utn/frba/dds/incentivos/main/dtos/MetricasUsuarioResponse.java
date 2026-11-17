package ar.edu.utn.frba.dds.incentivos.main.dtos;

import java.time.Period;
import java.util.Map;

public record MetricasUsuarioResponse(
        Number totalHistoricoDonaciones,
        Number posicionRanking,
        Map<String, Number> donacionesPorPeriodo,
        Map<String, Number> comparacionDeMeses,
        Number organizacionesQueAyudo
) {}
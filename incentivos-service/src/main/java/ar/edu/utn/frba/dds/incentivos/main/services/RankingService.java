package ar.edu.utn.frba.dds.incentivos.main.services;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;

import java.time.YearMonth;
import java.util.List;

public interface RankingService {
    RankingDonantes generarRanking(YearMonth periodo);

    RankingDonantes generarRanking();

    Number buscarPosicionDonanteEnRanking(Long id);
    RankingDonantes obtenerUltimoRanking(Integer top);
    RankingDonantes obtenerRankingPorPeriodo(Integer mes, Integer anio);
}

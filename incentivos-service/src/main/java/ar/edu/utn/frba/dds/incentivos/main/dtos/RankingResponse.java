package ar.edu.utn.frba.dds.incentivos.main.dtos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.DonanteEnRanking;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public record RankingResponse(
    Long id,
    Integer mes,
    Integer anio,
    LocalDate fechaRanking,
    List<DonanteRankingItemResponse> donantes,
    Integer totalDonantes
) {
    public static RankingResponse from(RankingDonantes ranking) {
        if (ranking == null) {
            return new RankingResponse(null, null, null, null, List.of(), 0);
        }
        YearMonth periodo = ranking.getPeriodo();
        List<DonanteRankingItemResponse> items = new ArrayList<>();
        if (ranking.getRanking() != null) {
            for (DonanteEnRanking der : ranking.getRanking()) {
                Donante d = der.getDonante();
                int donaciones = (d != null && periodo != null) ? d.getDonacionesEnPeriodo(periodo) : 0;
                long misiones = (d != null && periodo != null) ? d.getMisionesCompletadasEnPeriodo(periodo) : 0;
                items.add(new DonanteRankingItemResponse(
                    der.getPuesto() != null ? der.getPuesto() : 0,
                    d != null ? d.getId() : null,
                    d != null ? d.getIdDonanteDonacionesService() : null,
                    d != null && d.getNombreUsuario() != null ? d.getNombreUsuario() : "Donante",
                    donaciones,
                    misiones
                ));
            }
        }

        return new RankingResponse(
            ranking.getId(),
            periodo != null ? periodo.getMonthValue() : null,
            periodo != null ? periodo.getYear() : null,
            ranking.getFechaRanking(),
            items,
            items.size()
        );
    }
}

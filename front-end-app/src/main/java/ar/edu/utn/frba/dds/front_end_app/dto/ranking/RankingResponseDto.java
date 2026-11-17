package ar.edu.utn.frba.dds.front_end_app.dto.ranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RankingResponseDto(
    Long id,
    Integer mes,
    Integer anio,
    LocalDate fechaRanking,
    List<DonanteRankingItemDto> donantes,
    Integer totalDonantes
) {
    public List<DonanteRankingItemDto> getDonantesOEmpty() {
        return donantes != null ? donantes : List.of();
    }

    public int getTotal() {
        return totalDonantes != null ? totalDonantes : (donantes != null ? donantes.size() : 0);
    }
}

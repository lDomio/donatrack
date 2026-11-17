package ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos;

import ar.edu.utn.frba.dds.front_end_app.dto.ranking.GenerarRankingRequestDto;
import ar.edu.utn.frba.dds.front_end_app.dto.ranking.RankingResponseDto;
import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncentivosApiServiceTest {

  private static final String BASE = "http://incentivos";
  private static final String URL = BASE + "/ranking/generar";

  private WebApiCallerService webApiCaller;
  private IncentivosApiService incentivosApiService;

  @BeforeEach
  void setUp() {
    webApiCaller = Mockito.mock(WebApiCallerService.class);
    incentivosApiService = new IncentivosApiService(BASE, webApiCaller);
  }

  @Test
  @DisplayName("generarRanking hace POST con mes y año y devuelve la respuesta")
  void generarRankingHacePostConPeriodo() {
    RankingResponseDto response = new RankingResponseDto(
        1L, 10, 2026, LocalDate.of(2026, 10, 1), List.of(), 0);
    when(webApiCaller.post(eq(URL), any(GenerarRankingRequestDto.class), eq(RankingResponseDto.class)))
        .thenReturn(response);

    RankingResponseDto result = incentivosApiService.generarRanking(10, 2026);

    assertNotNull(result);
    assertEquals(10, result.mes());
    assertEquals(2026, result.anio());

    ArgumentCaptor<GenerarRankingRequestDto> captor = ArgumentCaptor.forClass(GenerarRankingRequestDto.class);
    verify(webApiCaller).post(eq(URL), captor.capture(), eq(RankingResponseDto.class));
    assertEquals(10, captor.getValue().mes());
    assertEquals(2026, captor.getValue().anio());
  }

  @Test
  @DisplayName("generarRanking devuelve null si el backend falla")
  void generarRankingDevuelveNullSiFalla() {
    when(webApiCaller.post(eq(URL), any(GenerarRankingRequestDto.class), eq(RankingResponseDto.class)))
        .thenThrow(new RuntimeException("incentivos caído"));

    assertNull(incentivosApiService.generarRanking(10, 2026));
  }
}

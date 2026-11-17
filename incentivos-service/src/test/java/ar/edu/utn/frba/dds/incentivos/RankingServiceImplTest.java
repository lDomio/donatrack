package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.repositories.RankingRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import ar.edu.utn.frba.dds.incentivos.main.services.impl.RankingServiceImpl;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceImplTest {

  @Mock
  private RankingRepository rankingRepository;

  @Mock
  private DonanteService donanteService;

  @InjectMocks
  private RankingServiceImpl rankingService;

  private static Donante donanteConMision(Long id, String nombre) {
    Donante donante = new Donante();
    donante.setId(id);
    donante.setIdDonanteDonacionesService(id);
    donante.setNombreUsuario(nombre);

    MisionCompletada mision = new MisionCompletada();
    mision.setFechaDeComplecion(LocalDate.of(2026, 10, 15));
    donante.setMisionesCompletadas(List.of(mision));
    donante.setDonaciones(List.of());
    return donante;
  }

  @Test
  @DisplayName("Genera el período con fecha del primer día y reemplaza el ranking existente")
  void generaPeriodoYReemplazaExistente() {
    Donante donante = donanteConMision(1L, "Ana");
    RankingDonantes existente = new RankingDonantes(LocalDate.of(2026, 10, 29), List.of(donante));

    when(rankingRepository.findByAnioAndMes(2026, 10)).thenReturn(List.of(existente));
    when(donanteService.findAll()).thenReturn(List.of(donante));
    when(rankingRepository.save(any(RankingDonantes.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RankingDonantes resultado = rankingService.generarRanking(YearMonth.of(2026, 10));

    assertNotNull(resultado);
    assertEquals(LocalDate.of(2026, 10, 1), resultado.getFechaRanking());
    assertEquals(YearMonth.of(2026, 10), resultado.getPeriodo());
    assertEquals(1, resultado.getRanking().size());
    assertEquals("Ana", resultado.getRanking().get(0).getDonante().getNombreUsuario());
    verify(rankingRepository).delete(existente);
    verify(rankingRepository).save(resultado);
  }

  @Test
  @DisplayName("Dos generaciones del mismo período eliminan el ranking previo antes de guardar")
  void dosGeneracionesReemplazanElRanking() {
    Donante donante = donanteConMision(1L, "Ana");
    RankingDonantes existente = new RankingDonantes(LocalDate.of(2026, 10, 29), List.of(donante));

    when(rankingRepository.findByAnioAndMes(2026, 10)).thenReturn(List.of(existente));
    when(donanteService.findAll()).thenReturn(List.of(donante));
    when(rankingRepository.save(any(RankingDonantes.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    rankingService.generarRanking(YearMonth.of(2026, 10));
    rankingService.generarRanking(YearMonth.of(2026, 10));

    verify(rankingRepository, org.mockito.Mockito.times(2)).delete(existente);
    verify(rankingRepository, org.mockito.Mockito.times(2)).save(any(RankingDonantes.class));
  }

  @Test
  @DisplayName("Buscar posición lee el último ranking y no genera uno nuevo")
  void buscarPosicionNoGeneraRanking() {
    Donante donante = donanteConMision(1L, "Ana");
    RankingDonantes ranking = new RankingDonantes(LocalDate.of(2026, 10, 1), List.of(donante));
    when(rankingRepository.findFirstByOrderByFechaRankingDesc()).thenReturn(Optional.of(ranking));

    Number posicion = rankingService.buscarPosicionDonanteEnRanking(1L);

    assertEquals(1, posicion.intValue());
    verify(rankingRepository, never()).save(any(RankingDonantes.class));
    verify(rankingRepository, never()).findByAnioAndMes(anyInt(), anyInt());
  }

  @Test
  @DisplayName("Buscar posición devuelve 0 si no hay ranking persistido")
  void buscarPosicionSinRankingDevuelveCero() {
    when(rankingRepository.findFirstByOrderByFechaRankingDesc()).thenReturn(Optional.empty());

    Number posicion = rankingService.buscarPosicionDonanteEnRanking(99L);

    assertEquals(0, posicion.intValue());
    verify(rankingRepository, never()).save(any(RankingDonantes.class));
  }
}

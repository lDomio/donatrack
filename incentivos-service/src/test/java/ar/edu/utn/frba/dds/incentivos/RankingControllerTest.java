package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.controllers.RankingController;
import ar.edu.utn.frba.dds.incentivos.main.dtos.DonanteRankingItemResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.GenerarRankingRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.RankingResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.DonanteEnRanking;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.services.RankingService;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingControllerTest {

  @Mock
  private RankingService rankingService;

  @InjectMocks
  private RankingController rankingController;

  private static Donante donante(Long id, String nombre, int misiones, int donaciones) {
    Donante donante = new Donante();
    donante.setId(id);
    donante.setIdDonanteDonacionesService(id);
    donante.setNombreUsuario(nombre);

    MisionCompletada mision = new MisionCompletada();
    mision.setFechaDeComplecion(LocalDate.of(2026, 10, 15));
    donante.setMisionesCompletadas(List.of(mision));
    if (misiones == 0) {
      donante.setMisionesCompletadas(List.of());
    }

    List<Donacion> donacionesList = new java.util.ArrayList<>();
    for (int i = 0; i < donaciones; i++) {
      Donacion donacion = new Donacion();
      donacion.setFechaDeCreacion(LocalDate.of(2026, 10, 5 + i));
      donacionesList.add(donacion);
    }
    donante.setDonaciones(donacionesList);
    return donante;
  }

  @Test
  @DisplayName("POST /ranking/generar devuelve el ranking con misiones y donaciones")
  void generaRankingYDevuelveMetricas() {
    Donante donante = donante(1L, "Ana", 1, 2);
    RankingDonantes ranking = new RankingDonantes(LocalDate.of(2026, 10, 1), List.of(donante));
    when(rankingService.generarRanking(YearMonth.of(2026, 10))).thenReturn(ranking);

    RankingResponse response = rankingController.generarRanking(new GenerarRankingRequest(10, 2026));

    assertEquals(2026, response.anio());
    assertEquals(10, response.mes());
    assertEquals(1, response.totalDonantes());
    assertEquals(1, response.donantes().size());
    DonanteRankingItemResponse item = response.donantes().get(0);
    assertEquals("Ana", item.nombreUsuario());
    assertEquals(1L, item.misionesCompletadas());
    assertEquals(2, item.cantidadDonaciones());
    verify(rankingService).generarRanking(YearMonth.of(2026, 10));
  }

  @Test
  @DisplayName("El request rechaza mes y año inválidos")
  void requestInvalido() {
    assertThrows(IllegalArgumentException.class, () -> new GenerarRankingRequest(13, 2026));
    assertThrows(IllegalArgumentException.class, () -> new GenerarRankingRequest(0, 2026));
    assertThrows(IllegalArgumentException.class, () -> new GenerarRankingRequest(10, 1999));
  }

  @Test
  @DisplayName("La respuesta respeta el orden persistido y no reordena por donaciones")
  void respetaOrdenPersistido() {
    Donante conMenosDonaciones = donante(1L, "Ana", 1, 1);
    Donante conMasDonaciones = donante(2L, "Beto", 1, 5);

    RankingDonantes ranking = new RankingDonantes();
    ranking.setFechaRanking(LocalDate.of(2026, 10, 1));
    ranking.setRanking(List.of(
        new DonanteEnRanking(conMenosDonaciones, 1),
        new DonanteEnRanking(conMasDonaciones, 2)
    ));

    RankingResponse response = RankingResponse.from(ranking);

    assertEquals("Ana", response.donantes().get(0).nombreUsuario());
    assertEquals(1, response.donantes().get(0).puesto());
    assertEquals("Beto", response.donantes().get(1).nombreUsuario());
    assertEquals(2, response.donantes().get(1).puesto());
    assertEquals(1, response.donantes().get(0).cantidadDonaciones());
    assertEquals(5, response.donantes().get(1).cantidadDonaciones());
  }

  @Test
  @DisplayName("El POST exige rol ADMINISTRADOR y permiso VER_RANKING")
  void tienePreAuthorizeDeAdministrador() throws NoSuchMethodException {
    Method method = RankingController.class.getMethod("generarRanking", GenerarRankingRequest.class);
    PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

    assertNotNull(preAuthorize);
    assertTrue(preAuthorize.value().contains("ADMINISTRADOR"));
    assertTrue(preAuthorize.value().contains("VER_RANKING"));
  }
}

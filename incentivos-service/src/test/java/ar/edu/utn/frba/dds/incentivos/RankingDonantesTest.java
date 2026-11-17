package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.DonanteEnRanking;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingDonantesTest {

  private static final LocalDate PRIMER_DIA_OCTUBRE = LocalDate.of(2026, 10, 1);

  private static Donante donante(Long id, String nombre, int misiones, int donaciones) {
    Donante donante = new Donante();
    donante.setId(id);
    donante.setIdDonanteDonacionesService(id);
    donante.setNombreUsuario(nombre);

    List<MisionCompletada> misionesCompletadas = new ArrayList<>();
    for (int i = 0; i < misiones; i++) {
      MisionCompletada mision = new MisionCompletada();
      mision.setFechaDeComplecion(LocalDate.of(2026, 10, 10 + i));
      misionesCompletadas.add(mision);
    }
    donante.setMisionesCompletadas(misionesCompletadas);

    List<Donacion> donacionesList = new ArrayList<>();
    for (int i = 0; i < donaciones; i++) {
      Donacion donacion = new Donacion();
      donacion.setFechaDeCreacion(LocalDate.of(2026, 10, 5 + i));
      donacionesList.add(donacion);
    }
    donante.setDonaciones(donacionesList);
    return donante;
  }

  @Test
  @DisplayName("Ordena por misiones completadas en el período, de mayor a menor")
  void ordenaPorMisionesDelPeriodo() {
    Donante dosMisiones = donante(1L, "Ana", 2, 1);
    Donante unaMision = donante(2L, "Beto", 1, 5);

    RankingDonantes ranking = new RankingDonantes(PRIMER_DIA_OCTUBRE, List.of(unaMision, dosMisiones));

    List<DonanteEnRanking> items = ranking.getRanking();
    assertEquals(2, items.size());
    assertEquals("Ana", items.get(0).getDonante().getNombreUsuario());
    assertEquals(1, items.get(0).getPuesto());
    assertEquals("Beto", items.get(1).getDonante().getNombreUsuario());
    assertEquals(2, items.get(1).getPuesto());
  }

  @Test
  @DisplayName("Desempata por donaciones del período cuando hay igual cantidad de misiones")
  void desempataPorDonaciones() {
    Donante masDonaciones = donante(1L, "Ana", 1, 3);
    Donante menosDonaciones = donante(2L, "Beto", 1, 1);

    RankingDonantes ranking = new RankingDonantes(PRIMER_DIA_OCTUBRE, List.of(menosDonaciones, masDonaciones));

    assertEquals("Ana", ranking.getRanking().get(0).getDonante().getNombreUsuario());
    assertEquals("Beto", ranking.getRanking().get(1).getDonante().getNombreUsuario());
  }

  @Test
  @DisplayName("Desempata por nombre cuando misiones y donaciones son iguales")
  void desempataPorNombre() {
    Donante beto = donante(1L, "Beto", 1, 1);
    Donante ana = donante(2L, "Ana", 1, 1);

    RankingDonantes ranking = new RankingDonantes(PRIMER_DIA_OCTUBRE, List.of(beto, ana));

    assertEquals("Ana", ranking.getRanking().get(0).getDonante().getNombreUsuario());
    assertEquals("Beto", ranking.getRanking().get(1).getDonante().getNombreUsuario());
  }

  @Test
  @DisplayName("Excluye donantes sin misiones en el período")
  void excluyeDonantesSinMisiones() {
    Donante conMision = donante(1L, "Ana", 1, 0);
    Donante sinMisiones = donante(2L, "Beto", 0, 10);

    RankingDonantes ranking = new RankingDonantes(PRIMER_DIA_OCTUBRE, List.of(conMision, sinMisiones));

    assertEquals(1, ranking.getRanking().size());
    assertEquals("Ana", ranking.getRanking().get(0).getDonante().getNombreUsuario());
  }

  @Test
  @DisplayName("No incluye misiones de otro período")
  void excluyeMisionesDeOtroPeriodo() {
    Donante donante = donante(1L, "Ana", 1, 0);
    MisionCompletada misionDeSeptiembre = new MisionCompletada();
    misionDeSeptiembre.setFechaDeComplecion(LocalDate.of(2026, 9, 30));
    donante.getMisionesCompletadas().add(misionDeSeptiembre);

    RankingDonantes ranking = new RankingDonantes(PRIMER_DIA_OCTUBRE, List.of(donante));

    assertEquals(1, ranking.getRanking().size());
    assertEquals(1, ranking.getRanking().get(0).getPuesto());
    assertEquals(YearMonth.of(2026, 10), ranking.getPeriodo());
  }

  @Test
  @DisplayName("Asigna puestos consecutivos desde 1")
  void asignaPuestosConsecutivos() {
    Donante primero = donante(1L, "Ana", 3, 0);
    Donante segundo = donante(2L, "Beto", 2, 0);
    Donante tercero = donante(3L, "Carla", 1, 0);

    RankingDonantes ranking = new RankingDonantes(PRIMER_DIA_OCTUBRE, List.of(tercero, primero, segundo));

    assertEquals(List.of(1, 2, 3),
        ranking.getRanking().stream().map(DonanteEnRanking::getPuesto).toList());
    assertTrue(ranking.getRanking().get(0).getDonante().getNombreUsuario().equals("Ana"));
  }
}

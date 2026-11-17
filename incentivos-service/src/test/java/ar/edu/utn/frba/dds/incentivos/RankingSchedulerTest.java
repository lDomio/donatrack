package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.scheduled.GenerarRankingSchedule;
import ar.edu.utn.frba.dds.incentivos.main.scheduled.ReinicioDeMisionesSchedule;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingSchedulerTest {

  @Test
  @DisplayName("El generador de ranking es bean y corre a las 03:00 del día 1 en zona Argentina")
  void generadorDeRankingEsBeanConCronCorrecto() throws NoSuchMethodException {
    assertTrue(GenerarRankingSchedule.class.isAnnotationPresent(Component.class));

    Method method = GenerarRankingSchedule.class.getMethod("generarRankingMensual");
    Scheduled scheduled = method.getAnnotation(Scheduled.class);

    assertNotNull(scheduled);
    assertEquals("0 0 3 1 * ?", scheduled.cron());
    assertEquals("America/Argentina/Buenos_Aires", scheduled.zone());
  }

  @Test
  @DisplayName("El reinicio de misiones corre a las 03:00 del día 1 en zona Argentina")
  void reinicioDeMisionesTieneCronCorrecto() throws NoSuchMethodException {
    Method method = ReinicioDeMisionesSchedule.class.getMethod("reiniciarMisionesMensuales");
    Scheduled scheduled = method.getAnnotation(Scheduled.class);

    assertNotNull(scheduled);
    assertEquals("0 0 3 1 * ?", scheduled.cron());
    assertEquals("America/Argentina/Buenos_Aires", scheduled.zone());
  }
}

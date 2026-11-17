package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.dtos.InsigniaResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.MisionResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class MisionResponseTest {

  @Test
  void fromDevuelveLaInsigniaComoDtoYNoComoEntidad() {
    Insignia insignia = new Insignia("Insignia de prueba");
    Mision mision = new Mision("Mision 1", insignia, null);
    MisionCompletada completada = new MisionCompletada(insignia, mision);

    MisionResponse response = MisionResponse.from(completada);

    assertInstanceOf(InsigniaResponse.class, response.insignia());
    assertEquals("Insignia de prueba", response.insignia().descripcion());
    assertEquals(LocalDate.now(), response.fecha());
  }
}

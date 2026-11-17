package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.MisionCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.generadoresMisionCompletada.GeneradorPaqueteDonanteMisionCompletadaN8N;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteN8N.PaqueteN8N;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneradorPaqueteDonanteMisionCompletadaN8NTest {

  @Test
  void elPayloadUsaElNombreDeLaMisionCompletadaYNoLaSiguiente() {
    Insignia insignia = new Insignia("Insignia de prueba");
    Mision completada = new Mision("Mision 1", insignia, null);
    Mision siguiente = new Mision("Mision 2", new Insignia("Insignia siguiente"), null);

    Donante donante = new Donante();
    donante.setNombreUsuario("Juan Pérez");

    MisionCompletada misionCompletada = new MisionCompletada(insignia, completada);
    MisionCompletadaEvento evento = new MisionCompletadaEvento(donante, misionCompletada, siguiente, insignia);

    PaqueteN8N paquete = (PaqueteN8N) new GeneradorPaqueteDonanteMisionCompletadaN8N()
        .generarPaqueteSiCorresponde(evento)
        .orElseThrow();

    assertEquals("Mision 1", paquete.getMensajeJson().get("tipoMision"));
    assertEquals("Insignia de prueba", paquete.getMensajeJson().get("nombreInsignia"));
  }
}

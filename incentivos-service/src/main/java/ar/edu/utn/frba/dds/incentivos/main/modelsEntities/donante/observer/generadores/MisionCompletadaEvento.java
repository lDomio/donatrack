package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;

public record MisionCompletadaEvento(
    Donante donante,
    MisionCompletada misionAnterior,
    Mision misionActual,
    Insignia insigniaOtorgada
) {
}

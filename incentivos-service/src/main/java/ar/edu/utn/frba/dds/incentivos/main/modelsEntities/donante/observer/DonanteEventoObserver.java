package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.CategoriaCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteCategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteMisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.MisionCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;


public class DonanteEventoObserver {
  Donante donante;

  List<IGeneradorPaqueteDonanteCategoriaCompletada> generadoresCategoriaCompletada;
  List<IGeneradorPaqueteDonanteMisionCompletada> generadoresMisionCompletada;

  public DonanteEventoObserver(Donante donante, List<IGeneradorPaqueteDonanteCategoriaCompletada> generadoresCategoriaCompletada, List<IGeneradorPaqueteDonanteMisionCompletada> generadoresMisionCompletada) {
    this.donante = donante;
    this.generadoresCategoriaCompletada = generadoresCategoriaCompletada;
    this.generadoresMisionCompletada = generadoresMisionCompletada;
  }

  public void notificarCategoriaCompletada(CategoriaCompletadaEvento categoriaCompletadaEvento){
    generadoresCategoriaCompletada.forEach(generador ->
        {
            generador.generarPaqueteSiCorresponde(categoriaCompletadaEvento).ifPresent( p ->this.donante.getPaquetesPendientes().add(p));
        }
    );
  }
  public void notificarMisionCompletada(MisionCompletadaEvento misionCompletadaEvento){
    generadoresMisionCompletada.forEach(generador ->
        {
          generador.generarPaqueteSiCorresponde(misionCompletadaEvento).ifPresent(p ->this.donante.getPaquetesPendientes().add(p));
        }
    );
  }
}

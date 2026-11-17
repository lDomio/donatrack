package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteCategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteMisionCompletada;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DonanteObserverFactory {

  private final List<IGeneradorPaqueteDonanteCategoriaCompletada> generadoresCategoria;
  private final List<IGeneradorPaqueteDonanteMisionCompletada> generadoresMision;

  public DonanteObserverFactory(List<IGeneradorPaqueteDonanteCategoriaCompletada> generadoresCategoria, List<IGeneradorPaqueteDonanteMisionCompletada> generadoresMision) {
    this.generadoresCategoria = generadoresCategoria;
    this.generadoresMision = generadoresMision;
  }

  public DonanteEventoObserver generar(Donante donante){
    return new DonanteEventoObserver(
        donante,
        generadoresCategoria,
        generadoresMision
    );
  }
}

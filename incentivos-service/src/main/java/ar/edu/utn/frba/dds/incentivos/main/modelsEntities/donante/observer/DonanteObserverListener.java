package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import jakarta.persistence.PostLoad;
import org.springframework.stereotype.Component;

// El observer es @Transient: hay que reasignarlo cada vez que se carga un donante desde la base.
@Component
public class DonanteObserverListener {
  private final DonanteObserverFactory observerFactory;

  public DonanteObserverListener(DonanteObserverFactory observerFactory) {
    this.observerFactory = observerFactory;
  }

  @PostLoad
  public void asignarObserver(Donante donante) {
    donante.setObserver(observerFactory.generar(donante));
  }
}

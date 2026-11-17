package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;


public class TipoNecesidadRecurrente implements TipoNecesidad{
  @Override
  public void aumentarCantidad(Necesidad necesidad, Double cantidad, List<Periodo> periodos) {
    this.actualizarPeriodos(necesidad, periodos);
    necesidad.getHistorialPeriodos().getLast().setCantidadDonada(
      necesidad.getHistorialPeriodos().getLast().getCantidadDonada() + cantidad
    );

  }

  @Override
  public Double getCantidadDonada(Necesidad necesidad, List<Periodo> periodos) {
    this.actualizarPeriodos(necesidad, periodos);
    return necesidad.getHistorialPeriodos().getLast().getCantidadDonada();
  }

  @Override
  public TipoNecesidadEnum getTipoNecesidad() {
    return TipoNecesidadEnum.RECURRENTE;
  }

  @Override
  public Periodo getUltimoPeriodo(Necesidad necesidad, List<Periodo> periodos) {
    this.actualizarPeriodos(necesidad, periodos);
    return necesidad.getHistorialPeriodos().getLast();
  }

  @Override
  public void desasignar(Donacion donacion) {
    donacion.getPeriodo().setCantidadDonada(
        donacion.getPeriodo().getCantidadDonada() - donacion.getCantidad()
    );
  }


  public void actualizarPeriodos(Necesidad necesidad, List<Periodo> periodos){
    LocalDate fechaActual = LocalDate.now();
    while(periodos.isEmpty()
          || periodos.getLast().getFechaFin().isBefore(fechaActual)){
        if (periodos.isEmpty()){
          periodos.add(
              new Periodo(
                  necesidad.getPeriodoCorrespondiente(),
                  necesidad.getInicioRecurrencia(),
                  0.0,
                  necesidad)
          );
        }
        else{
          periodos.add(
              new Periodo(
                  necesidad.getPeriodoCorrespondiente(),
                  periodos.getLast().getFechaFin(),
                  0.0,
                  necesidad)
          );
        }

      }
  }

  @Override
  public String obtenerNombre() {
    return "Recurrente";
  }
}

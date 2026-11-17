package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import java.util.List;
import org.springframework.context.annotation.Bean;


public interface TipoNecesidad {
  public void aumentarCantidad(Necesidad necesidad, Double cantidad, List<Periodo> periodos);

  public Double getCantidadDonada(Necesidad necesidad, List<Periodo> periodos);

  public TipoNecesidadEnum getTipoNecesidad();

  public Periodo getUltimoPeriodo(Necesidad necesidad, List<Periodo> periodos);

  public void desasignar(Donacion donacion);

  public void actualizarPeriodos(Necesidad necesidad, List<Periodo> periodos);

  public String obtenerNombre();
}
package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import java.util.List;
import org.springframework.context.annotation.Bean;


public class TipoNecesidadExtraordinaria implements TipoNecesidad{

  @Override
  public void aumentarCantidad(Necesidad necesidad, Double cantidad, List<Periodo> periodos) {
    necesidad.setCantidadDonadaTotal(
        necesidad.getCantidadDonadaTotal() + cantidad
    );
  }

  @Override
  public Double getCantidadDonada(Necesidad necesidad, List<Periodo> periodos) {
    return necesidad.getCantidadDonadaTotal();
  }

  @Override
  public TipoNecesidadEnum getTipoNecesidad() {
    return TipoNecesidadEnum.EXTRAORDINARIA;
  }

  @Override
  public Periodo getUltimoPeriodo(Necesidad necesidad, List<Periodo> periodos) {
    return null;
  }

  @Override
  public void desasignar(Donacion donacion) {
    Necesidad necesidad = donacion.getNecesidad();
    necesidad.setCantidadDonadaTotal(
        necesidad.getCantidadDonadaTotal() - donacion.getCantidad()
    );
  }

  @Override
  public void actualizarPeriodos(Necesidad necesidad, List<Periodo> periodos) {
    return;
  }

  @Override
  public String obtenerNombre() {
    return "Extraordinaria";
  }


}

package ar.edu.utn.frba.dds.donaciones.main.config;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadExtraordinaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadRecurrente;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TiposConfig {

  @Bean(name = "RECURRENTE")
  public TipoNecesidad tipoRecurrente(){
      return new TipoNecesidadRecurrente();
  }
  @Bean(name = "EXTRAORDINARIA")
  public TipoNecesidad tipoExtraordinario(){
    return new TipoNecesidadExtraordinaria();
  }
}

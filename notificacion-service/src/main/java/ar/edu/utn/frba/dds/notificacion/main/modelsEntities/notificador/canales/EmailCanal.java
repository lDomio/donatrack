package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.Notificador;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.AdapterEnviarMail;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailCanal implements Notificador {

  private final AdapterEnviarMail adapter;

  @Autowired
  public EmailCanal(AdapterEnviarMail adapter) {
    this.adapter = adapter;
  }

  @Override
  public boolean soporta(TipoDeContacto tipoContacto) {
    return TipoDeContacto.EMAIL.equals(tipoContacto);
  }

  @Override
  public boolean enviar(Notificacion notificacion, String contacto) {
    return this.adapter.enviar(notificacion, contacto);
  }
}

package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.AdapterEnviarSms;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.Notificador;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MensajeCanal implements Notificador {

  private final AdapterEnviarSms adapter;

  @Autowired
  public MensajeCanal(AdapterEnviarSms adapter) {
    this.adapter = adapter;
  }

  @Override
  public boolean soporta(TipoDeContacto tipoContacto) {
    return TipoDeContacto.SMS.equals(tipoContacto);
  }

  @Override
  public boolean enviar(Notificacion notificacion, String contacto) {
    return this.adapter.enviar(notificacion, contacto);
  }
}

package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.adapters.AdapterEnviarWhatsapp;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.Notificador;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhatsappCanal implements Notificador {

  private final AdapterEnviarWhatsapp adapter;

  @Autowired
  public WhatsappCanal(AdapterEnviarWhatsapp adapter) {
    this.adapter = adapter;
  }

  @Override
  public boolean soporta(TipoDeContacto tipoContacto) {
    return TipoDeContacto.WHATSAPP.equals(tipoContacto);
  }

  @Override
  public boolean enviar(Notificacion notificacion, String contacto) {
    return this.adapter.enviar(notificacion, contacto);
  }
}

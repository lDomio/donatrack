package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;

public interface Notificador {
  boolean soporta(TipoDeContacto tipoContacto);
  boolean enviar(Notificacion notificacion, String contacto);
}

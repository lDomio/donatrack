package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

public enum TipoDeContacto {
  EMAIL,
  WHATSAPP,
  SMS,
  TELEFONO
}

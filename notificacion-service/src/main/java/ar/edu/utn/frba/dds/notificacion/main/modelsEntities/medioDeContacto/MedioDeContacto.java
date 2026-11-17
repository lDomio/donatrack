package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medioDeContacto")
public class MedioDeContacto {
  public MedioDeContacto(String valor, TipoDeContacto tipoDeContacto, boolean esPreferido) {
    this.valor = valor;
    this.tipoDeContacto = tipoDeContacto;
    this.esPreferido = esPreferido;
  }
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "valor")
  private String valor;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "tipoDeContacto")
  private TipoDeContacto tipoDeContacto;

  @Column(name = "esPreferido")
  private boolean esPreferido;

  public boolean esPreferido() {
    return esPreferido;
  }
}

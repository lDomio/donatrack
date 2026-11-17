package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity @Table(name = "paquete")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class Paquete {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fue_enviado")
  protected Boolean fueEnviado = false;

  @Getter
  @Column(name = "fecha_de_creacion")
  protected LocalDateTime fechaDeCreacion;

  public void marcarComoEnviado(){
    this.fueEnviado = true;
  };
}

package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.estadosNotificacion;

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

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estadoNotificacion")
public class EstadoNotificacion {
  public EstadoNotificacion(LocalDateTime fechaHora, TipoDeEstado tipo) {
    this.fechaHora = fechaHora;
    this.tipo = tipo;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fechaHora")
  private LocalDateTime fechaHora;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "tipo")
  private TipoDeEstado tipo;


}

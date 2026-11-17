package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.Destinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.estadosNotificacion.EstadoNotificacion;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.estadosNotificacion.TipoDeEstado;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificacion")
public class Notificacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "destinatario_id", referencedColumnName = "id_destinatario")
  private Destinatario destinatario;

  @Column(name = "mensaje")
  private String mensaje;

  @Embedded
  private PlantillaCanal plantillaCanal;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "notificacion_id", referencedColumnName = "id")
  private List<EstadoNotificacion> estadoNotificaciones = new ArrayList<>();


  public void registrarEvento(TipoDeEstado tipoEstado) {
    this.estadoNotificaciones.add(new EstadoNotificacion(LocalDateTime.now(), tipoEstado));
  }

  public EstadoNotificacion obtenerUltimoEstado() {
    if (estadoNotificaciones.isEmpty()) return null;
    return estadoNotificaciones.get(estadoNotificaciones.size() - 1);
  }
}

package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteN8N;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.NoArgsConstructor;

@Entity @DiscriminatorValue("N8N")
@NoArgsConstructor
public class PaqueteN8N extends Paquete {
  public enum EstadoMensaje {
    PENDIENTE, ENVIADO, ERROR
  }
  @Enumerated(EnumType.STRING)
  @Column(name = "estado_mensaje")
  private EstadoMensaje estado;

  @Getter
  @Column(name = "cantidad_intentos")
  private Integer cantidadDeIntentos;

  @Getter
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "mensaje_JSON")
  private Map<String, String> mensajeJson;


  public PaqueteN8N(Map<String, String> mensajeJson) {
    this.mensajeJson = mensajeJson;
    this.cantidadDeIntentos = 0;
    this.estado = EstadoMensaje.PENDIENTE;
    this.fechaDeCreacion = LocalDateTime.now();
  }

  @Override
  public void marcarComoEnviado(){
    super.marcarComoEnviado();
    this.estado=EstadoMensaje.ENVIADO;
  }
  public void avisarEnvioFallido(){
    this.cantidadDeIntentos++;
  }
}

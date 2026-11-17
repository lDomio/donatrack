package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteBroker;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Broker")
@NoArgsConstructor
@Getter
public class PaqueteBroker extends Paquete {

  @Column(name = "routing_key")
  String routingKey;

  @Lob
  @Column(name = "payload", columnDefinition = "LONGBLOB")
  byte[] payload;

  @Embedded
  PropertiesBroker properties;

  @Column(name = "headers")
  @JdbcTypeCode(SqlTypes.JSON)
  Map<String, String> headers;

  public PaqueteBroker( String routingKey, byte[] payload, PropertiesBroker properties, Map<String, String> headers) {
    this.fechaDeCreacion = LocalDateTime.now();
    this.routingKey = routingKey;
    this.payload = payload;
    this.properties = properties;
    this.headers = headers;
  }


}

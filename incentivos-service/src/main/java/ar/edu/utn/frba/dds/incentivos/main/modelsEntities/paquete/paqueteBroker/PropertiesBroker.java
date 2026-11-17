package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteBroker;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PropertiesBroker(
    @Column(name = "content_type")
    String contentType,

    @Column(name = "correlation_id")
    String correlationId,

    @Column(name = "prioridad")
    Integer prioridad,

    @Column(name = "reply_to")
    String replyTo
) {}

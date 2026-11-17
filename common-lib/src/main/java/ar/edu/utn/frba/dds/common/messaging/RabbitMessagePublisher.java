package ar.edu.utn.frba.dds.common.messaging;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "donatrack.events.exchange")
public class RabbitMessagePublisher implements MessagePublisher {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RabbitMessagePublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final GeneradorAccessTokenServicios generadorAccessTokenServicios;

    public RabbitMessagePublisher(RabbitTemplate rabbitTemplate, @Value("${donatrack.events.exchange:eventos}") String exchange, GeneradorAccessTokenServicios generadorAccessTokenServicios) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
      this.generadorAccessTokenServicios = generadorAccessTokenServicios;
    }

    public void notifyUser(String severity, NotificationDto notificacion) {
        String routingKey = solve(severity);
        try {
            publish(routingKey,notificacion);
        } catch (Exception e) {
            log.error("Falló la notificación del evento {} de tipo: {} y routingKey: {}", notificacion.eventId(), severity, routingKey);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void publish(String routingKey, Object dto) {
        this.rabbitTemplate.convertAndSend(this.exchange, routingKey, dto, message ->{
            message.getMessageProperties().setHeader("Authorization", "Bearer " + generadorAccessTokenServicios.generarAccessToken());
            return message;
        });
    }

    private String solve(String severity) {
        return switch (severity.trim().toUpperCase()) {
            case "CRITICA" -> "notificacion.critica";
            case "GENERAL" -> "notificacion.general";
            case "BULK" -> "notificacion.bulk";
            default -> throw new IllegalArgumentException("severity desconocido: " + severity);
        };
    }
}

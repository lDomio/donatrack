package ar.edu.utn.frba.dds.common.messaging;

public interface MessagePublisher {
    void notifyUser(String severity, NotificationDto notificationDto);
    void publish(String routingKey, Object dto);
}

package ar.edu.utn.frba.dds.common.messaging;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record NotificationDto(UUID eventId, EventsTypeEnum eventsTypeEnum, String origin, LocalDateTime date,
                              String name,
                              List<ContactMethodDto> contacts, String message, String donationDetail, String entityName,
                              Map<String, String> extraData) {
}

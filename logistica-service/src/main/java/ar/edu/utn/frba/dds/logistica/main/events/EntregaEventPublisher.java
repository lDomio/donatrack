package ar.edu.utn.frba.dds.logistica.main.events;

import ar.edu.utn.frba.dds.common.messaging.RabbitMessagePublisher;
import ar.edu.utn.frba.dds.logistica.main.events.dto.EntregaEventDto;
import ar.edu.utn.frba.dds.logistica.main.events.dto.MensajeDonanteDto;
import ar.edu.utn.frba.dds.logistica.main.events.dto.MensajeEntidadBeneficiariaDto;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntregaEventPublisher {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EntregaEventPublisher.class);

    private final RabbitMessagePublisher rabbitMessagePublisher;
    private final String routingKeyEntregaExito;
    private final String routingKeyEntregaNoSatisfactoria;

    public EntregaEventPublisher(
        RabbitMessagePublisher rabbitMessagePublisher, @Value("${donatrack.events.routing-keys.entrega-exito}") String routingKeyEntregaExito,
        @Value("${donatrack.events.routing-keys.entrega-no-satisfactoria}") String routingKeyEntregaNoSatisfactoria
    ) {
      this.rabbitMessagePublisher = rabbitMessagePublisher;

      this.routingKeyEntregaExito = routingKeyEntregaExito;
        this.routingKeyEntregaNoSatisfactoria = routingKeyEntregaNoSatisfactoria;
    }

    public void publicarEntregaExitosa(Entrega entrega) {
        EntregaEventDto dto = convertirADto(
                entrega,
                "Tu donación fue entregada con éxito",
                "La donación asignada a su institución fue entregada con éxito"
        );
        publicar(dto, routingKeyEntregaExito, "Entrega Exitosa");
    }

    public void publicarEntregaNoSatisfactoria(Entrega entrega, String observaciones) {
        String detalle = (observaciones == null || observaciones.isBlank()) ? "" : ": " + observaciones;
        EntregaEventDto dto = convertirADto(
                entrega,
                "La entrega de tu donación no pudo realizarse" + detalle,
                "La entrega de la donación asignada a su institución no pudo realizarse" + detalle
        );
        publicar(dto, routingKeyEntregaNoSatisfactoria, "Entrega No Satisfactoria");
    }

    private void publicar(EntregaEventDto dto, String routingKey, String descripcion) {
        try {
            this.rabbitMessagePublisher.publish(routingKey, dto);
            log.info("Evento de {} publicado", descripcion);
        } catch (Exception e) {
            log.error("Error publicando evento de {}", descripcion, e);
            throw new RuntimeException(e);
        }
    }

    private EntregaEventDto convertirADto(Entrega entrega, String mensajeDonante, String mensajeEntidad) {
        List<MensajeDonanteDto> mensajeDonanteDtos = entrega.getDonacionesAEntregar().stream()
                .map(donacion -> new MensajeDonanteDto(donacion.getDonanteId(), mensajeDonante))
                .toList();
        List<MensajeEntidadBeneficiariaDto> mensajeEntidadDtos = List.of(
                new MensajeEntidadBeneficiariaDto(entrega.getRazonSocialEntidadBeneficiaria(), mensajeEntidad)
        );
        return new EntregaEventDto(entrega.getId(), mensajeEntidadDtos, mensajeDonanteDtos);
    }
}

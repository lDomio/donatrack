package ar.edu.utn.frba.dds.logistica.main.events;

import ar.edu.utn.frba.dds.common.messaging.RabbitMessagePublisher;
import ar.edu.utn.frba.dds.logistica.main.events.dto.InicioRutaEventDto;
import ar.edu.utn.frba.dds.logistica.main.events.dto.MensajeDonanteDto;
import ar.edu.utn.frba.dds.logistica.main.events.dto.MensajeEntidadBeneficiariaDto;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Parada;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InicioRutaEventPublisher {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InicioRutaEventPublisher.class);

    private final RabbitMessagePublisher rabbitMessagePublisher;
    private final String routingKey;

    public InicioRutaEventPublisher(
        RabbitMessagePublisher rabbitMessagePublisher,
        @Value("${donatrack.events.routing-keys.inicio-ruta}") String routingKey
    ) {
      this.rabbitMessagePublisher = rabbitMessagePublisher;
      this.routingKey = routingKey;
    }

    public void publicarInicioRuta(Ruta ruta) {
        InicioRutaEventDto dto = convertirADtoSalida(ruta);
        try {
                                            // Estos dos parámetros son nuestro topic
            this.rabbitMessagePublisher.publish(this.routingKey, dto);
            log.info("Evento de Inicio de Ruta publicado");
        } catch (Exception e) {
            log.error("Error publicando evento de Inicio de Ruta"); // agregar info de ruta para stacktrace
            throw new RuntimeException(e);
        }
    }

    public InicioRutaEventDto convertirADtoSalida(Ruta ruta) {
        List<Entrega> entregas = ruta.getParadas().stream().map(Parada::getEntrega).toList();
        List<MensajeDonanteDto> mensajeDonanteDtos = entregas.stream()
                .flatMap(entrega -> entrega.getDonacionesAEntregar().stream() )
                .map(donacion -> new MensajeDonanteDto(donacion.getDonanteId(), "Se inició una ruta con una donación tuya"))
                .toList();
        List<MensajeEntidadBeneficiariaDto> mensajeEntidadBeneficiariaDtos = entregas.stream()
                .map(entrega -> new MensajeEntidadBeneficiariaDto(entrega.getRazonSocialEntidadBeneficiaria(), "Se inició una ruta con una donación para su institución"))
                .toList();
        // TODO pasar también la URI del mapa interactivo con seguimiento de la entrega en el mensaje
        return new InicioRutaEventDto(ruta.getId(), mensajeEntidadBeneficiariaDtos, mensajeDonanteDtos);
    }
}

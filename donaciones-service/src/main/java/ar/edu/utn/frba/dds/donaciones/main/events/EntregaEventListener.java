package ar.edu.utn.frba.dds.donaciones.main.events;

import ar.edu.utn.frba.dds.donaciones.main.events.dtos.EntregaEventDto;
import ar.edu.utn.frba.dds.donaciones.main.events.dtos.InicioRutaEventDtoSalida;
import ar.edu.utn.frba.dds.donaciones.main.services.DonacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class EntregaEventListener {

    private static final Logger log = LoggerFactory.getLogger(EntregaEventListener.class);
    private final DonacionService donacionService;

    public EntregaEventListener(DonacionService donacionService) {
        this.donacionService = donacionService;
    }

    @RabbitListener(queues = "${donatrack.events.queues.entrega-exito}")
    public void onEntregaExitosa(EntregaEventDto event) {
        log.info("Procesando entrega exitosa");
        donacionService.notificarDonanteEntidadBeneficiaria(convertirDtoSalida(event));
    }

    @RabbitListener(queues = "${donatrack.events.queues.entrega-no-satisfactoria}")
    public void onEntregaNoSatisfactoria(EntregaEventDto event) {
        log.info("Procesando entrega no satisfactoria");
        donacionService.notificarDonanteEntidadBeneficiaria(convertirDtoSalida(event));
    }

    private List<InicioRutaEventDtoSalida> convertirDtoSalida(EntregaEventDto event) {
        return Stream.concat(
                event.mensajeDonantes().stream().map(
                        mensajeDonanteDto -> new InicioRutaEventDtoSalida(donacionService.recuperarMedioContactoDonante(mensajeDonanteDto.donanteId()), mensajeDonanteDto.mensaje())
                ),
                event.mensajeEntidadesBeneficiarias().stream().map(
                        mensajeEntidadBeneficiariaDto -> new InicioRutaEventDtoSalida(donacionService.recuperarMedioContactoEntidadBeneficiaria(mensajeEntidadBeneficiariaDto.razonSocial()), mensajeEntidadBeneficiariaDto.mensaje())
                )
        ).toList();
    }
}

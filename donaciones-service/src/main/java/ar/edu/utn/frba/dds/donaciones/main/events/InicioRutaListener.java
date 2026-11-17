package ar.edu.utn.frba.dds.donaciones.main.events;

import ar.edu.utn.frba.dds.donaciones.main.events.dtos.InicioRutaEventDto;
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
public class InicioRutaListener {

    private static final Logger log = LoggerFactory.getLogger(InicioRutaListener.class);
    private final DonacionService donacionService;

    public InicioRutaListener(DonacionService donacionService) {
        this.donacionService = donacionService;
    }

    @RabbitListener(queues = "${donatrack.events.queues.inicio-ruta}")
    public void onInicioRuta(InicioRutaEventDto event) {
        log.info("Procesando inicio de ruta");
        List<InicioRutaEventDtoSalida> dtoSalida = convertirDtoSalida(event);
        donacionService.notificarDonanteEntidadBeneficiaria(dtoSalida);
    }

    public List<InicioRutaEventDtoSalida> convertirDtoSalida(InicioRutaEventDto event) {
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

package ar.edu.utn.frba.dds.donaciones.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Reloj del servidor para calculos dependientes del "mes actual"
 * (conteo de entidades ayudadas). Zona America/Argentina/Buenos_Aires
 * para que el mes coincida con el del negocio. Inyectable para fijarlo
 * en tests de borde de mes.
 */
@Configuration
public class ClockConfig {

    public static final ZoneId ZONA = ZoneId.of("America/Argentina/Buenos_Aires");

    @Bean
    public Clock clock() {
        return Clock.system(ZONA);
    }
}

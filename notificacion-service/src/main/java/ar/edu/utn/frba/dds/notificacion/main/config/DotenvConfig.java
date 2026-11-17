package ar.edu.utn.frba.dds.notificacion.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:.env.twilio.local", ignoreResourceNotFound = true)
public class DotenvConfig {
}

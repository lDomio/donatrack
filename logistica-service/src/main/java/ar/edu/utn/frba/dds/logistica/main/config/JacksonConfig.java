package ar.edu.utn.frba.dds.logistica.main.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Fundamental para que entienda las fechas de Java 8 (LocalDateTime, Instant, etc.)
        mapper.registerModule(new JavaTimeModule());

        // 2. Apagamos el ajuste de zona horaria (para que parsee la 'Z' y la guarde como LocalDateTime local)
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        // Opcional pero recomendado: Evita que la app explote si la API de rutas manda campos extra
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}


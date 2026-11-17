package ar.edu.utn.frba.dds.incentivos.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"ar.edu.utn.frba.dds.incentivos", "ar.edu.utn.frba.dds.common"})
@EnableScheduling // Habilita la ejecución de tareas programadas
@EnableMethodSecurity
public class IncentivosServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(IncentivosServiceApplication.class)
                .profiles("dev", "incentivos")
                .run(args);
    }

}

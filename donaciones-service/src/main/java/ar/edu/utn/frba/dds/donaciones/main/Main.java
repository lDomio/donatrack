package ar.edu.utn.frba.dds.donaciones.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(scanBasePackages = {"ar.edu.utn.frba.dds.donaciones", "ar.edu.utn.frba.dds.common"})
@EnableScheduling
@EnableMethodSecurity
public class Main {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class)
                .profiles("dev", "donaciones")
                .run(args);
    }
}
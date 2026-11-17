package ar.edu.utn.frba.dds.logistica.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@ComponentScan(basePackages = {"ar.edu.utn.frba.dds.logistica", "ar.edu.utn.frba.dds.common"})
@EnableAsync
@EnableScheduling
@EnableMethodSecurity
public class LogisticaApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "logistica");
        SpringApplication.run(LogisticaApplication.class, args);
    }
}


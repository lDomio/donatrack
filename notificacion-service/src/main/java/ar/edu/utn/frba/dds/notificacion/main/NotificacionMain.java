package ar.edu.utn.frba.dds.notificacion.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(scanBasePackages = {"ar.edu.utn.frba.dds.notificacion", "ar.edu.utn.frba.dds.common"})
@EnableMethodSecurity
public class NotificacionMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(NotificacionMain.class)
                .profiles("dev", "notificaciones")
                .run(args);
    }
}

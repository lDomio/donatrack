package ar.edu.utn.frba.dds.autenticacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ar.edu.utn.frba.dds.autenticacion", "ar.edu.utn.frba.dds.common"})
public class AutenticacionServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AutenticacionServiceApplication.class, args);
  }
}

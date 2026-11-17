package ar.edu.utn.frba.dds.logistica.main.config;

import dev.morphia.Datastore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Configuration
public class ResetDatabase {

  @Bean
  @Profile("logistica")
  @Order(1)
  public CommandLineRunner resetearBaseDeDatos(Datastore datastore) {
    return args -> {
      System.out.println("⚠️ Conectado mediante Morphia. Vaciando base de datos...");

      datastore.getDatabase().drop();
      System.out.println("✅ Base de datos MongoDB limpiada exitosamente (Drop completo)");
    };
  }
}
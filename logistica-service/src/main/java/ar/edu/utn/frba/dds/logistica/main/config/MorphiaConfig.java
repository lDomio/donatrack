package ar.edu.utn.frba.dds.logistica.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

import java.util.List;

@Configuration
public class MorphiaConfig {

  @Value("${mongo.host}")
  private String host;
  @Value("${mongo.port}")
  private int port;
  @Value("${mongo.username}")
  private String username;
  @Value("${mongo.password}")
  private String password;
  @Value("${mongo.database}")
  private String database;

  @Bean
  public Datastore datastore() {
    String connectionString;

    if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
      connectionString = String.format("mongodb://%s:%s@%s:%s", username, password, host, port);
    } else {
      connectionString = String.format("mongodb://%s:%s", host, port);
    }
    MongoClient mongoClient = MongoClients.create(connectionString);

    dev.morphia.config.MorphiaConfig config = dev.morphia.config.MorphiaConfig.load()
        .database(database)
        .packages(List.of("ar.edu.utn.frba.dds.logistica.main.modelsEntities"));

    return Morphia.createDatastore(mongoClient, config);
  }
}
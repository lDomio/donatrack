package ar.edu.utn.frba.dds.front_end_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
// TODO: revisar si este exclude se elimina o si lo vamos a necesitar.
// Lo comenté porque rompía la compilación: estas clases vienen de las librerías de JPA/JDBC,
// que el front no tiene en su pom.xml (no usa base de datos). Sin esas librerías Spring
// tampoco intenta configurar una base, así que hoy el exclude no hace falta.
// Si en algún momento se agrega JPA/JDBC al front y no queremos que configure la base,
// hay que descomentarlo.
// import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
// import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
//
// @SpringBootApplication(exclude = {
// 		DataSourceAutoConfiguration.class,
// 		HibernateJpaAutoConfiguration.class
// })

@SpringBootApplication
@EnableScheduling
public class FrontEndAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontEndAppApplication.class, args);
	}

}

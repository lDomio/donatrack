package ar.edu.utn.frba.dds.donaciones.main.config;

import ar.edu.utn.frba.dds.common.security.JwtUtil;
import ar.edu.utn.frba.dds.common.security.RabbitJwtSecurityInterceptor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration("donacionesRabbitMQConfig")
public class RabbitMQConfig {
    @Bean
    public Queue inicioRutaQueue(@Value("${donatrack.events.queues.inicio-ruta}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding inicioRutaBinding(Queue inicioRutaQueue, DirectExchange eventosExchange,
                                     @Value("${donatrack.events.routing-keys.inicio-ruta}") String routingKey) {
        return BindingBuilder.bind(inicioRutaQueue).to(eventosExchange).with(routingKey);
    }

    @Bean
    public Queue entregaExitoQueue(@Value("${donatrack.events.queues.entrega-exito}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding entregaExitoBinding(Queue entregaExitoQueue, DirectExchange eventosExchange,
                                       @Value("${donatrack.events.routing-keys.entrega-exito}") String routingKey) {
        return BindingBuilder.bind(entregaExitoQueue).to(eventosExchange).with(routingKey);
    }

    @Bean
    public Queue entregaNoSatisfactoriaQueue(@Value("${donatrack.events.queues.entrega-no-satisfactoria}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding entregaNoSatisfactoriaBinding(Queue entregaNoSatisfactoriaQueue, DirectExchange eventosExchange,
                                                 @Value("${donatrack.events.routing-keys.entrega-no-satisfactoria}") String routingKey) {
        return BindingBuilder.bind(entregaNoSatisfactoriaQueue).to(eventosExchange).with(routingKey);
    }

}

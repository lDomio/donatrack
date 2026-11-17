package ar.edu.utn.frba.dds.notificacion.main.config;

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

@Configuration("notificacionRabbitMQConfig")
public class RabbitMQConfig {

//    @Bean
//    public Queue notificacionesQueue(@Value("${donatrack.events.queues.notificaciones}") String name) {
//        return new Queue(name, true);
//    }
//
//    @Bean
//    public Queue notificacionQueue(@Value("${donatrack.events.queues.notificacion}") String name) {
//        return new Queue(name, true);
//    }
//
//    @Bean
//    public Binding notificacionesBinding(Queue notificacionesQueue, DirectExchange eventosExchange,
//                                         @Value("${donatrack.events.routing-keys.notificaciones}") String routingKey) {
//        return BindingBuilder.bind(notificacionesQueue).to(eventosExchange).with(routingKey);
//    }
//
//    @Bean
//    public Binding notificacionBinding(Queue notificacionQueue, DirectExchange eventosExchange,
//                                       @Value("${donatrack.events.routing-keys.notificaciones}") String routingKey) {
//        return BindingBuilder.bind(notificacionQueue).to(eventosExchange).with(routingKey);
//    }

    @Bean
    public Queue notificacionCriticaQueue(@Value("${donatrack.events.queues.notificacion.critica}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding notificacionCriticaBinding(Queue notificacionCriticaQueue, DirectExchange eventosExchange, @Value("${donatrack.events.routing-keys.notificacion.critica}") String routingKey) {
        return BindingBuilder.bind(notificacionCriticaQueue).to(eventosExchange).with(routingKey);
    }

    @Bean
    public Queue notificacionGeneralQueue(@Value("${donatrack.events.queues.notificacion.general}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding notificacionGeneralBinding(Queue notificacionGeneralQueue, DirectExchange eventosExchange, @Value("${donatrack.events.routing-keys.notificacion.general}") String routingKey) {
        return BindingBuilder.bind(notificacionGeneralQueue).to(eventosExchange).with(routingKey);
    }

    @Bean
    public Queue notificacionBulkQueue(@Value("${donatrack.events.queues.notificacion.bulk}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding notificacionBulkBinding(Queue notificacionBulkQueue, DirectExchange eventosExchange, @Value("${donatrack.events.routing-keys.notificacion.bulk}") String routingKey) {
        return BindingBuilder.bind(notificacionBulkQueue).to(eventosExchange).with(routingKey);
    }

}

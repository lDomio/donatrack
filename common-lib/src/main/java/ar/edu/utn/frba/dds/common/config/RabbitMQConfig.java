package ar.edu.utn.frba.dds.common.config;

import ar.edu.utn.frba.dds.common.security.JwtUtil;
import ar.edu.utn.frba.dds.common.security.RabbitJwtSecurityInterceptor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "donatrack.events.exchange")
public class RabbitMQConfig {
    // en caso de querer que este microservicio se suscriba a eventos
    // tenemos que agregar los beans de Queue y Binding

    // Dentro de esta clase tenemos 3 beans "mágicos"
    @Bean
    // Declaramos quién va a ser nuestro canal de comunicación, donde vamos a estar publicando
    public DirectExchange eventosExchange(@Value("${donatrack.events.exchange}") String name) {
        return new DirectExchange(name, true, false);
    }

    @Bean
    // Convierte el DTO a json
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    // RabbitTemplate va a ser quien nos permite hacer la conexión directamente con RabbitMQ, es quien nos permite mandarle el mensaje al broker
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        JwtUtil jwtUtil,
        MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setAdviceChain(new RabbitJwtSecurityInterceptor(jwtUtil));

        return factory;
    }
}

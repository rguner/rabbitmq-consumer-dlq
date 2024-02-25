package com.guner.consumer.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Value("${single-consumer.exchange}")
    private String topicExchange;

    @Value("${single-consumer.queue}")
    private String queueSingle;

    @Value("${single-consumer.routing-key}")
    private String routingKeySingle;

    @Value("${single-consumer.exchange}-DLX")
    private String deadLetterExchange;

    @Value("${single-consumer.queue}-DLQ")
    private String deadLetterSingleQueue;

    @Value("${single-consumer.exchange}-exhausted")
    private String exhaustedExchange;

    @Value("${single-consumer.queue}-exhausted")
    private String exhaustedSingleQueue;



    @Bean
    public Queue queueSingle() {
        return QueueBuilder.durable(queueSingle)
                .deadLetterExchange(deadLetterExchange) // to dead letter exchange
                .deadLetterRoutingKey("deadLetterRoutingKey")
                //.withArgument("x-dead-letter-exchange", deadLetterExchange)
                //.withArgument("x-dead-letter-exchange", "")
                //.withArgument("x-dead-letter-routing-key", "deadLetterRoutingKey")
                .build();
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(topicExchange);
    }


    @Bean
    public Binding bindingSingle() {
        return BindingBuilder
                .bind(queueSingle())
                .to(topicExchange())
                .with(routingKeySingle);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterSingleQueue)
                .ttl(5000)
                .deadLetterExchange(topicExchange) // to original exchange
                .deadLetterRoutingKey(routingKeySingle) // to original routing key
                .build();
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchange);
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("deadLetterRoutingKey");
    }

    @Bean
    public Queue exhaustedQueue() {
        return QueueBuilder.durable(exhaustedSingleQueue)
                .build();
    }

    @Bean
    DirectExchange exhaustedExchange() {
        return new DirectExchange(exhaustedExchange);
    }

    @Bean
    Binding exhaustedBinding() {
        return BindingBuilder
                .bind(exhaustedQueue())
                .to(exhaustedExchange())
                .with(".#");
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter());
        factory.setDefaultRequeueRejected(false); // for dead letter queue
        return factory;
    }

}

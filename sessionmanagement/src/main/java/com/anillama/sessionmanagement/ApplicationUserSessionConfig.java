package com.anillama.sessionmanagement;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApplicationUserSessionConfig {
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.registerSession}")
    private String registerSessionQueue;

    @Value("${rabbitmq.routing-keys.internal-registerSession}")
    private String internalRegisterSessionRoutingKey;

    @Value("${rabbitmq.queues.checkSession}")
    private String checkSessionQueue;

    @Value("${rabbitmq.routing-keys.internal-checkSession}")
    private String internalCheckSessionRoutingKey;

    @Value("${rabbitmq.queues.removeSession}")
    private String removeSessionQueue;

    @Value("${rabbitmq.routing-keys.internal-removeSession}")
    private String internalRemoveSessionRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue registerSessionQueue() {
        return new Queue(this.registerSessionQueue);
    }

    @Bean
    public Queue checkSessionQueue() {
        return new Queue(this.checkSessionQueue);
    }

    @Bean
    public Queue removeSessionQueue() {
        return new Queue(this.removeSessionQueue);
    }

    @Bean
    public Binding internalToRegisterSessionBinding() {
        return BindingBuilder
                .bind(registerSessionQueue())
                .to(internalTopicExchange())
                .with(this.internalRegisterSessionRoutingKey);
    }

    @Bean
    public Binding internalToCheckSessionBinding() {
        return BindingBuilder
                .bind(checkSessionQueue())
                .to(internalTopicExchange())
                .with(this.internalCheckSessionRoutingKey);
    }

    @Bean
    public Binding internalToRemoveSessionBinding() {
        return BindingBuilder
                .bind(registerSessionQueue())
                .to(internalTopicExchange())
                .with(this.internalRemoveSessionRoutingKey);
    }
}

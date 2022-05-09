package com.anillama.profile;

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
public class ProfileConfig {
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.removeProfile}")
    private String removeProfileQueue;

    @Value("${rabbitmq.routing-keys.internal-removeProfile}")
    private String internalRemoveProfileRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue removeProfileQueue() {
        return new Queue(this.removeProfileQueue);
    }

    @Bean
    public Binding internalToRemoveProfileBinding() {
        return BindingBuilder
                .bind(removeProfileQueue())
                .to(internalTopicExchange())
                .with(this.internalRemoveProfileRoutingKey);
    }
}

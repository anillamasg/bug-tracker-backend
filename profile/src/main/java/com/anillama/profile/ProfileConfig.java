package com.anillama.profile;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Getter
public class ProfileConfig {
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.getNameProfile}")
    private String getNameProfile;

    @Value("${rabbitmq.queues.removeProfile}")
    private String removeProfileQueue;

    @Value("${rabbitmq.queues.createProfileFromQueue}")
    private String createProfileFromQueue;

    @Value("${rabbitmq.routing-keys.internal-getNameProfile}")
    private String internalGetNameProfileRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-removeProfile}")
    private String internalRemoveProfileRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-createProfileFromQueue}")
    private String internalCreateProfileFromQueueRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue removeProfileQueue() {
        return new Queue(this.removeProfileQueue);
    }

    @Bean
    public Queue getNameProfile() {
        return new Queue(this.getNameProfile);
    }

    @Bean
    public Queue createProfileFromQueue() {
        return new Queue(this.createProfileFromQueue);
    }

    @Bean
    public Binding internalToRemoveProfileBinding() {
        return BindingBuilder
                .bind(removeProfileQueue())
                .to(internalTopicExchange())
                .with(this.internalRemoveProfileRoutingKey);
    }

    @Bean
    public Binding internalToGetProfileBinding() {
        return BindingBuilder
                .bind(getNameProfile())
                .to(internalTopicExchange())
                .with(this.internalGetNameProfileRoutingKey);
    }

    @Bean
    public Binding internalToCreateProfileFromQueueBinding() {
        return BindingBuilder
                .bind(createProfileFromQueue())
                .to(internalTopicExchange())
                .with(this.internalCreateProfileFromQueueRoutingKey);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS");
            }
        };
    }
}

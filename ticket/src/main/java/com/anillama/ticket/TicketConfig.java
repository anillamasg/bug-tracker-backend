package com.anillama.ticket;

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
public class TicketConfig {
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.removeAllTicketsByProject}")
    private String removeAllTicketsByProjectQueue;

    @Value("${rabbitmq.routing-keys.internal-removeAllTicketsByProject}")
    private String internalRemoveAllTicketsByProjectRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue removeAllUserProjectsByProjectQueue() {
        return new Queue(this.removeAllTicketsByProjectQueue);
    }

    @Bean
    public Binding internalToRemoveAllUserProjectByProjectBinding() {
        return BindingBuilder
                .bind(removeAllUserProjectsByProjectQueue())
                .to(internalTopicExchange())
                .with(this.internalRemoveAllTicketsByProjectRoutingKey);
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

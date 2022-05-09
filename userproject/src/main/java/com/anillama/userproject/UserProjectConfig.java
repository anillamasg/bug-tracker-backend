package com.anillama.userproject;

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
public class UserProjectConfig {
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.registerUserProject}")
    private String registerUserProjectQueue;

    @Value("${rabbitmq.routing-keys.internal-registerUserProject}")
    private String internalRegisterUserProjectRoutingKey;

    @Value("${rabbitmq.queues.checkUserProject}")
    private String checkUserProjectQueue;

    @Value("${rabbitmq.routing-keys.internal-checkUserProject}")
    private String internalCheckUserProjectRoutingKey;

    @Value("${rabbitmq.queues.removeUserProject}")
    private String removeUserProjectQueue;

    @Value("${rabbitmq.routing-keys.internal-removeUserProject}")
    private String internalRemoveUserProjectRoutingKey;

    @Value("${rabbitmq.queues.removeAllUserProjectsByProject}")
    private String removeAllUserProjectsByProjectQueue;

    @Value("${rabbitmq.routing-keys.internal-removeAllUserProjectsByProject}")
    private String internalRemoveAllUserProjectsByProjectRoutingKey;

    @Value("${rabbitmq.queues.removeAllUserProjectsByUser}")
    private String removeAllUserProjectsByUserQueue;

    @Value("${rabbitmq.routing-keys.internal-removeAllUserProjectsByUser}")
    private String internalRemoveAllUserProjectsByUserRoutingKey;

    @Value("${rabbitmq.queues.getAllProjectsForUser}")
    private String getAllProjectsForUserQueue;

    @Value("${rabbitmq.routing-keys.internal-getAllProjectsForUser}")
    private String internalGetAllProjectsForUserRoutingKey;

    @Value("${rabbitmq.queues.getAllUsersOfProject}")
    private String getAllUsersOfProjectQueue;

    @Value("${rabbitmq.routing-keys.internal-getAllUsersOfProject}")
    private String internalGetAllUsersOfProjectRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue registerUserProjectQueue() {
        return new Queue(this.registerUserProjectQueue);
    }

    @Bean
    public Queue checkUserProjectQueue() {
        return new Queue(this.checkUserProjectQueue);
    }

    @Bean
    public Queue removeUserProjectQueue() {
        return new Queue(this.removeUserProjectQueue);
    }

    @Bean
    public Queue removeAllUserProjectsByProjectQueue() {
        return new Queue(this.removeAllUserProjectsByProjectQueue);
    }

    @Bean
    public Queue removeAllUserProjectsByUserQueue() {
        return new Queue(this.removeAllUserProjectsByUserQueue);
    }

    @Bean
    public Queue getAllProjectsForUserQueue() {
        return new Queue(this.getAllProjectsForUserQueue);
    }

    @Bean
    public Queue getAllUsersOfProjectQueue() {
        return new Queue(this.getAllUsersOfProjectQueue);
    }

    @Bean
    public Binding internalToRegisterUserProjectBinding() {
        return BindingBuilder
                .bind(registerUserProjectQueue())
                .to(internalTopicExchange())
                .with(this.internalRegisterUserProjectRoutingKey);
    }

    @Bean
    public Binding internalToCheckUserProjectBinding() {
        return BindingBuilder
                .bind(checkUserProjectQueue())
                .to(internalTopicExchange())
                .with(this.internalCheckUserProjectRoutingKey);
    }

    @Bean
    public Binding internalToRemoveUserProjectBinding() {
        return BindingBuilder
                .bind(removeUserProjectQueue())
                .to(internalTopicExchange())
                .with(this.internalRemoveUserProjectRoutingKey);
    }

    @Bean
    public Binding internalToRemoveAllUserProjectByProjectBinding() {
        return BindingBuilder
                .bind(removeAllUserProjectsByProjectQueue())
                .to(internalTopicExchange())
                .with(this.internalRemoveAllUserProjectsByProjectRoutingKey);
    }

    @Bean
    public Binding internalToRemoveAllUserProjectsByUserBinding() {
        return BindingBuilder
                .bind(removeAllUserProjectsByUserQueue())
                .to(internalTopicExchange())
                .with(this.internalRemoveAllUserProjectsByUserRoutingKey);
    }

    @Bean
    public Binding internalToGetAllProjectsForUserQueueBinding() {
        return BindingBuilder
                .bind(getAllProjectsForUserQueue())
                .to(internalTopicExchange())
                .with(this.internalGetAllProjectsForUserRoutingKey);
    }

    @Bean
    public Binding internalToGetAllUsersOfProjectQueueBinding() {
        return BindingBuilder
                .bind(getAllUsersOfProjectQueue())
                .to(internalTopicExchange())
                .with(this.internalGetAllUsersOfProjectRoutingKey);
    }
}

package com.anillama.userproject.rabbitmq;

import com.anillama.clients.userproject.UserProjectRequest;
import com.anillama.userproject.UserProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class UserProjectConsumer {
    private final UserProjectService userProjectService;

    @RabbitListener(queues = "${rabbitmq.queues.registerUserProject}")
    public void createUserProject(UserProjectRequest request) {
        log.info("Consumed at createUserProject {} from queue", request);
        userProjectService.createUserProject(request);
    }

    @RabbitListener(queues = "${rabbitmq.queues.checkUserProject}")
    public String accessCheck(UserProjectRequest request) {
        log.info("Consumed at accessCheck {} from queue", request);
        return userProjectService.accessCheck(request);
    }

    @RabbitListener(queues = "${rabbitmq.queues.getAllProjectsForUser}")
    public List<Long> getAllProjectsForUser(Long userId) {
        log.info("Consumed at getAllProjectsForUser {} from queue", userId);
        return userProjectService.getAllProjectsForUser(userId);
    }

    @RabbitListener(queues = "${rabbitmq.queues.getAllUsersOfProject}")
    public List<Long> getAllUsersOfProject(Long projectId) {
        log.info("Consumed at getAllUsersOfProject {} from queue", projectId);
        return userProjectService.getAllUsersOfProject(projectId);
    }

    @RabbitListener(queues = "${rabbitmq.queues.removeUserProject}")
    public void removeUserProject(UserProjectRequest request) {
        log.info("Consumed at removeUserProject {} from queue", request);
        userProjectService.removeUserProject(request);
    }

    @RabbitListener(queues = "${rabbitmq.queues.removeAllUserProjectsByProject}")
    public void removeAllUserProjectsByProject(Long projectId) {
        log.info("Consumed at removeAllUserProjectsByProject {} from queue", projectId);
        userProjectService.removeAllUserProjectsByProject(projectId);
    }

    @RabbitListener(queues = "${rabbitmq.queues.removeAllUserProjectsByUser}")
    public void removeAllUserProjectsByUser(Long userId) {
        log.info("Consumed at removeAllUserProjectsByUser {} from queue", userId);
        userProjectService.removeAllUserProjectsByUser(userId);
    }
}

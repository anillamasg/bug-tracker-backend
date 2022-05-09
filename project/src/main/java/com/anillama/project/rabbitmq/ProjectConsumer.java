package com.anillama.project.rabbitmq;

import com.anillama.project.ProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ProjectConsumer {
    private final ProjectService projectService;

    @RabbitListener(queues = "${rabbitmq.queues.projectExists}")
    public String projectExists(Long projectId) {
        log.info("projectExists ==> {}", projectId);
        return projectService.projectExists(projectId);
    }
}

package com.anillama.sessionmanagement.rabbitmq;

import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import com.anillama.sessionmanagement.ApplicationUserSessionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.anillama.clients.application.ApplicationUtil.AUTHORIZATION;

@Component
@Slf4j
@AllArgsConstructor
public class ApplicationUserSessionConsumer {
    private final ApplicationUserSessionService applicationUserSessionService;

    @RabbitListener(queues = "${rabbitmq.queues.registerSession}")
    public void registerUserSession(ApplicationUserSessionRequest request) {
        log.info("Consumed at registerUserSession {} from queue", request);
        applicationUserSessionService.registerUserSession(request);
    }

    @RabbitListener(queues = "${rabbitmq.queues.checkSession}")
    public String checkTokenValidity(ApplicationUserSessionRequest request) {
        log.info("Consumed at checkTokenValidity {} from queue", request);
        return applicationUserSessionService.checkTokenValidity(request);
    }

    @RabbitListener(queues = "${rabbitmq.queues.removeSession}")
    public ResponseEntity<String> removeUserSession(@RequestHeader(name = AUTHORIZATION) String authorizationHeader) {
        log.info("Consumed at removeUserSession {} from queue");
        return applicationUserSessionService.removeUserSession(authorizationHeader);
    }
}

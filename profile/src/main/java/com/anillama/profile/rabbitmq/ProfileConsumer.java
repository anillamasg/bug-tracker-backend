package com.anillama.profile.rabbitmq;

import com.anillama.profile.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ProfileConsumer {
    private final ProfileService profileService;

    @RabbitListener(queues = "${rabbitmq.queues.removeProfile}")
    public void removeProfile(Long userId) {
        log.info("removeProfile ==> {}", userId);
        profileService.removeProfile(userId);
    }
}

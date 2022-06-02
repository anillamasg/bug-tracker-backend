package com.anillama.profile.rabbitmq;

import com.anillama.clients.profile.ProfileRequest;
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

    @RabbitListener(queues = "${rabbitmq.queues.getNameProfile}")
    public String getNameFromProfile(Long userId) {
        log.info("getNameFromProfile ==> {}", userId);
        return profileService.getNameFromProfile(userId);
    }

    @RabbitListener(queues = "${rabbitmq.queues.createProfileFromQueue}")
    public void createProfileFromQueue(ProfileRequest profileRequest) {
        log.info("createProfileFromQueue ==> {}", profileRequest);
        profileService.createProfileFromQueue(profileRequest);
    }
}

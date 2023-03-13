package com.anillama.sessionmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationUserSessionDao {
    private final RedisTemplate<String, Object> template;
    private static final String HASH_KEY = "UserSession";
    private static final Logger LOGGER = LogManager.getLogger(ApplicationUserSessionDao.class);

    public ApplicationUserSessionDao(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    public void save(ApplicationUserSession session) {
        LOGGER.info("Session: {}", session);
        template.opsForHash().put(HASH_KEY, String.valueOf(session.getUserId()), session.getToken());
        LOGGER.info("Session saved: {}", session);
    }

    public String findTokenByUserId(Long userId) {
        LOGGER.info("User id: {}", userId);
        String token = (String) template.opsForHash().get(HASH_KEY, String.valueOf(userId));
        LOGGER.info("Token retrieved: {}", token);
        return token;
    }
}

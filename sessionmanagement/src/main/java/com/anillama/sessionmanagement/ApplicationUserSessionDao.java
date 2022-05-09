package com.anillama.sessionmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationUserSessionDao {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;
    private static final String HASH_KEY = "UserSession";

    public ApplicationUserSession save(ApplicationUserSession session) {
        template.opsForHash().put(HASH_KEY, String.valueOf(session.getUserId()), session.getToken());
        return session;
    }

    public String findTokenByUserId(Long userId) {
        return (String) template.opsForHash().get(HASH_KEY, String.valueOf(userId));
    }
}

package com.anillama.clients.sessionmanagement;

import com.anillama.amqp.RabbitMQMessageProducer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.anillama.clients.application.ApplicationUtil.*;

@Service
@Slf4j
public class ApplicationUserSessionValidateService {
    private final JwtConfig jwtConfig;
    private final RabbitMQMessageProducer messageProducer;

    @Autowired
    public ApplicationUserSessionValidateService(JwtConfig jwtConfig, RabbitMQMessageProducer messageProducer) {
        this.jwtConfig = jwtConfig;
        this.messageProducer = messageProducer;
    }

    public ApplicationUserSessionRequest validateUser(String authorizationHeader) {
        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");

        Jws<Claims> claimsJws;
        Claims body;
        Integer id;
        ApplicationUserSessionRequest sessionRequest;
        String check, role;
        ApplicationUserSessionRequest sessionResponse;
        try {
            claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes()))
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            log.info(e.getStackTrace() + " => " + e.getMessage());
            return new ApplicationUserSessionRequest(null, INVALID, "");
        }
        body = claimsJws.getBody();
        id = (Integer) body.get("id");
        role = (String) body.get("role");
        sessionRequest = new ApplicationUserSessionRequest(Long.valueOf(id.longValue()), token, role);
        check = messageProducer.publishAndReceive(sessionRequest, INTERNAL_EXCHANGE, INTERNAL_CHECK_SESSION_ROUTING_KEY);
        sessionResponse = new ApplicationUserSessionRequest(VALID.equals(check) ? id.longValue() : null, check, role);
        return sessionResponse;
    }
}

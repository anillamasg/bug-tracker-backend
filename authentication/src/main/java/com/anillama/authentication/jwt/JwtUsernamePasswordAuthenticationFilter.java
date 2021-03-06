package com.anillama.authentication.jwt;

import com.anillama.amqp.RabbitMQMessageProducer;
import com.anillama.authentication.auth.ApplicationUser;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import com.anillama.clients.sessionmanagement.JwtConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import static com.anillama.clients.application.ApplicationUtil.*;

@Slf4j
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final RabbitMQMessageProducer messageProducer;

    @Autowired
    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, SecretKey secretKey, RabbitMQMessageProducer messageProducer) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.messageProducer = messageProducer;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UsernamePasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), UsernamePasswordAuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            Authentication authenticate = authenticationManager.authenticate(authentication);
            return authenticate;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        Long userId = ((ApplicationUser) authResult.getPrincipal()).getId();
        String role = ((ApplicationUser) authResult.getPrincipal()).getRole();
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("id", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(secretKey)
                .compact();

        String name = messageProducer.publishAndReceive(userId, INTERNAL_EXCHANGE, INTERNAL_GET_NAME_PROFILE_ROUTING_KEY);
        JSONObject data = new JSONObject();
        try {
            data.put(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);
            data.put("id", userId);
            data.put("name", name);
            data.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(data.toString());

        ApplicationUserSessionRequest sessionRequest = new ApplicationUserSessionRequest(userId, token, role);
        messageProducer.publish(sessionRequest, INTERNAL_EXCHANGE, INTERNAL_REGISTER_SESSION_ROUTING_KEY);
    }
}

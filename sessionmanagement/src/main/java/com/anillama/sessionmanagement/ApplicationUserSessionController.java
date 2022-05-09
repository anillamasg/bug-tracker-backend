package com.anillama.sessionmanagement;

import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.anillama.clients.application.ApplicationUtil.AUTHORIZATION;

@RestController
@Slf4j
public class ApplicationUserSessionController {
    private final ApplicationUserSessionService userSessionService;

    @Autowired
    public ApplicationUserSessionController(ApplicationUserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @PostMapping(value = "/registerSession")
    public void registerUserSession(@RequestBody ApplicationUserSessionRequest request) {
        log.info("registerUserSession request for ApplicationUser {} {}", request.userId(), request.token());
        userSessionService.registerUserSession(request);
    }

    @PostMapping(value = "/checkSession")
    public String checkTokenValidity(@RequestBody ApplicationUserSessionRequest request) {
        log.info("checkTokenValidity request for ApplicationUser {} {}", request.userId(), request.token());
        return userSessionService.checkTokenValidity(request);
    }

    @PostMapping(value = "/getSession")
    public String getSession(@RequestBody ApplicationUserSessionRequest request) {
        log.info("getSession request for ApplicationUser {} {}", request.userId(), request.token());
        return userSessionService.getToken(request);
    }

    @PostMapping(value = "/removeSession")
    public ResponseEntity<String> removeUserSession(@RequestHeader(name = AUTHORIZATION) String authorizationHeader) {
        log.info("removeUserSession request for ApplicationUser {}");
        return userSessionService.removeUserSession(authorizationHeader);
    }
}

package com.anillama.sessionmanagement;

import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.anillama.clients.application.ApplicationUtil.INVALID;
import static com.anillama.clients.application.ApplicationUtil.VALID;

@Service
public class ApplicationUserSessionService {
    private final ApplicationUserSessionDao applicationUserSessionDao;
    private final ApplicationUserSessionValidateService sessionValidateService;

    @Autowired
    public ApplicationUserSessionService(ApplicationUserSessionDao applicationUserSessionDao, ApplicationUserSessionValidateService sessionValidateService) {
        this.applicationUserSessionDao = applicationUserSessionDao;
        this.sessionValidateService = sessionValidateService;
    }

    public void registerUserSession(ApplicationUserSessionRequest request) {
        ApplicationUserSession session = ApplicationUserSession.builder()
                .userId(request.userId())
                .token(request.token())
                .build();

        applicationUserSessionDao.save(session);
    }

    public String checkTokenValidity(ApplicationUserSessionRequest request) {
        String savedToken = applicationUserSessionDao.findTokenByUserId(request.userId());
        if(savedToken==null) return INVALID;
        if(savedToken.equals(request.token())) return VALID;
        return INVALID;
    }

    public String getToken(ApplicationUserSessionRequest request) {
        String savedToken = applicationUserSessionDao.findTokenByUserId(request.userId());
        return savedToken;
    }

    public ResponseEntity<String> removeUserSession(String authorizationHeader) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            ApplicationUserSession session = ApplicationUserSession.builder()
                    .userId(response.userId())
                    .token(null)
                    .build();

            applicationUserSessionDao.save(session);
            return ResponseEntity.ok("Session removed successfully.");
        }
        return new ResponseEntity("Invalid user. Session removal failed.", HttpStatus.UNAUTHORIZED);
    }
}

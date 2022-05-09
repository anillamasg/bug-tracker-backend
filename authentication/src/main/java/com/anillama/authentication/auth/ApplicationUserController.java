package com.anillama.authentication.auth;

import com.anillama.clients.userproject.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.anillama.clients.application.ApplicationUtil.AUTHORIZATION;

@RestController
@Slf4j
public class ApplicationUserController {
    private final ApplicationUserService userService;

    @Autowired
    public ApplicationUserController(ApplicationUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody UserRequest request) {
        log.info("Requested createUser ==> {}", request);
        return userService.createUser(authorizationHeader, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeUser(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id) {
        log.info("Requested removeUser ==> {}", id);
        return userService.removeUser(authorizationHeader, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id) {
        log.info("Requested getUser ==> ", id);
        return userService.getUser(authorizationHeader, id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestHeader(name = AUTHORIZATION) String authorizationHeader) {
        log.info("Requested getAllUsers ==> ");
        return userService.getAllUsers(authorizationHeader);
    }

    @GetMapping("/allByProject/{projectId}")
    public ResponseEntity<List<UserResponse>> getAllUsersByProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long projectId) {
        log.info("Requested getAllUsers ==> {}", projectId);
        return userService.getAllUsersByProject(authorizationHeader, projectId);
    }
}

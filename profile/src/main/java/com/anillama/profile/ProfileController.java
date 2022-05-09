package com.anillama.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.anillama.clients.application.ApplicationUtil.AUTHORIZATION;

@RestController
@Slf4j
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<String> createProfile(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody ProfileRequest request) {
        log.info("createProfile => {}", request);
        return profileService.createProfile(authorizationHeader, request);
    }

    @GetMapping
    public ResponseEntity<Profile> getProfile(@RequestHeader(name = AUTHORIZATION) String authorizationHeader) {
        log.info("getProfile => {}");
        return profileService.getProfile(authorizationHeader);
    }

    @PutMapping
    public ResponseEntity<String> updateProfile(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody ProfileRequest request) {
        log.info("updateProfile => {}", request);
        return profileService.updateProfile(authorizationHeader, request);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteProfile(@RequestHeader(name = AUTHORIZATION) String authorizationHeader) {
        log.info("deleteProfile => {}");
        return profileService.deleteProfile(authorizationHeader);
    }
}

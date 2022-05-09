package com.anillama.profile;

import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.anillama.clients.application.ApplicationUtil.*;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ApplicationUserSessionValidateService sessionValidateService;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, ApplicationUserSessionValidateService sessionValidateService) {
        this.profileRepository = profileRepository;
        this.sessionValidateService = sessionValidateService;
    }

    public ResponseEntity<String> createProfile(String authorizationHeader, ProfileRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            Profile profile = profileRepository.getProfileByUserId(response.userId());
            if (profile != null)
                return new ResponseEntity(serviceAlreadyExists(PROFILE, CREATION), HttpStatus.BAD_REQUEST);
            profile = Profile.builder()
                    .userId(response.userId())
                    .name(request.name())
                    .email(request.email())
                    .build();
            profileRepository.save(profile);
            return ResponseEntity.ok(serviceSuccessful(PROFILE, CREATION));
        }
        return new ResponseEntity(invalidUserFailed(PROFILE, CREATION), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Profile> getProfile(String authorizationHeader) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            Profile profile = profileRepository.getProfileByUserId(response.userId());
            if (profile == null)
                return new ResponseEntity(serviceDoesNotExist(PROFILE, RETRIEVAL), HttpStatus.BAD_REQUEST);
            return ResponseEntity.ok(profile);
        }
        return new ResponseEntity(invalidUserFailed(PROFILE, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<String> updateProfile(String authorizationHeader, ProfileRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            Profile savedProfile = profileRepository.getProfileByUserId(response.userId());
            if (savedProfile == null)
                return new ResponseEntity(serviceDoesNotExist(PROFILE, UPDATE), HttpStatus.BAD_REQUEST);
            savedProfile.setName(request.name());
            savedProfile.setEmail(request.email());
            profileRepository.save(savedProfile);
            return ResponseEntity.ok(serviceSuccessful(PROFILE, UPDATE));
        }
        return new ResponseEntity(invalidUserFailed(PROFILE, UPDATE), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<String> deleteProfile(String authorizationHeader) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            Integer profileDeleted = profileRepository.deleteProfileByUserId(response.userId());
            if (profileDeleted.equals(0))
                return new ResponseEntity(serviceDoesNotExist(PROFILE, DELETION), HttpStatus.BAD_REQUEST);
            return ResponseEntity.ok(serviceSuccessful(PROFILE, DELETION));
        }
        return new ResponseEntity(invalidUserFailed(PROFILE, DELETION), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public void removeProfile(Long userId) {
        profileRepository.deleteProfileByUserId(userId);
    }
}

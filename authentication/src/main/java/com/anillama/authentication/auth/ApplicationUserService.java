package com.anillama.authentication.auth;

import com.anillama.amqp.RabbitMQMessageProducer;
import com.anillama.clients.profile.ProfileRequest;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionValidateService;
import com.anillama.clients.userproject.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.anillama.clients.application.ApplicationUtil.*;

@Service
public class ApplicationUserService implements UserDetailsService {
    private final ApplicationUserRepository userRepository;
    private final ApplicationUserSessionValidateService sessionValidateService;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQMessageProducer messageProducer;

    @Autowired
    public ApplicationUserService(ApplicationUserRepository userRepository, ApplicationUserSessionValidateService sessionValidateService, PasswordEncoder passwordEncoder, RabbitMQMessageProducer messageProducer) {
        this.userRepository = userRepository;
        this.sessionValidateService = sessionValidateService;
        this.passwordEncoder = passwordEncoder;
        this.messageProducer = messageProducer;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        ApplicationUser applicationUser = userRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(String.format("User %s is not found", username));
        }
        return applicationUser;
    }

    public ResponseEntity<String> createUser(String authorizationHeader, UserRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(APPLICATION_USER, CREATION), HttpStatus.BAD_REQUEST);

            ApplicationUser user = userRepository.findByUsername(request.email());
            if (user != null)
                return new ResponseEntity(serviceAlreadyExists(APPLICATION_USER, CREATION), HttpStatus.BAD_REQUEST);
            user = ApplicationUser.builder()
                    .username(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .role(request.role())
                    .build();
            user = userRepository.save(user);
            messageProducer.publish(new ProfileRequest(user.getId(), request.name(), request.email()), INTERNAL_EXCHANGE, INTERNAL_CREATE_PROFILE_FROM_QUEUE_ROUTING_KEY);
            return ResponseEntity.ok(serviceSuccessful(APPLICATION_USER, CREATION));
        }
        return new ResponseEntity(invalidUserFailed(APPLICATION_USER, CREATION), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<UserResponse> getUser(String authorizationHeader, Long id) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            Optional<ApplicationUser> optional = Optional.ofNullable(userRepository.findById(id).orElse(null));
            if (optional.isEmpty())
                return new ResponseEntity(serviceDoesNotExist(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            String name = messageProducer.publishAndReceive(optional.get().getId(), INTERNAL_EXCHANGE, INTERNAL_GET_NAME_PROFILE_ROUTING_KEY);
            UserResponse userResponse = new UserResponse(optional.get().getId(), optional.get().getUsername(), optional.get().getRole(), name);
            return ResponseEntity.ok(userResponse);
        }
        return new ResponseEntity(invalidUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<UserResponse> getUserSelf(String authorizationHeader, Long id) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            Optional<ApplicationUser> optional = Optional.ofNullable(userRepository.findById(id).orElse(null));
            if (optional.isEmpty())
                return new ResponseEntity(serviceDoesNotExist(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            String name = messageProducer.publishAndReceive(optional.get().getId(), INTERNAL_EXCHANGE, INTERNAL_GET_NAME_PROFILE_ROUTING_KEY);
            UserResponse userResponse = new UserResponse(optional.get().getId(), optional.get().getUsername(), optional.get().getRole(), name);
            return ResponseEntity.ok(userResponse);
        }
        return new ResponseEntity(invalidUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<UserResponse>> getAllUsers(String authorizationHeader) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            List<ApplicationUser> applicationUsers = userRepository.findAll();
            if (applicationUsers == null)
                return new ResponseEntity(serviceDoesNotExist(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            List<UserResponse> userResponseList = new ArrayList<>();
            for (ApplicationUser user : applicationUsers) {
                String name = messageProducer.publishAndReceive(user.getId(), INTERNAL_EXCHANGE, INTERNAL_GET_NAME_PROFILE_ROUTING_KEY);
                userResponseList.add(new UserResponse(user.getId(), user.getUsername(), user.getRole(), name));
            }
            return ResponseEntity.ok(userResponseList);
        }
        return new ResponseEntity(invalidUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<UserResponse>> getAllUsersByProject(String authorizationHeader, Long projectId) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
//            if (!response.role().equals(ADMIN))
//                return new ResponseEntity(unauthorizedUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            String check = messageProducer.publishAndReceive(projectId, INTERNAL_EXCHANGE, INTERNAL_PROJECT_EXISTS_ROUTING_KEY);
            if (check.equals(INVALID))
                return new ResponseEntity(serviceDoesNotExist(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            List<Long> userIds = messageProducer.publishAndReceiveList(projectId, INTERNAL_EXCHANGE, INTERNAL_GET_ALL_USERS_OF_PROJECT_ROUTING_KEY);
            List<ApplicationUser> applicationUsers = userRepository.findAllByUserIds(userIds);
            if (applicationUsers == null)
                return new ResponseEntity(serviceDoesNotExist(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            List<UserResponse> userResponseList = new ArrayList<>();
            for (ApplicationUser user : applicationUsers) {
                String name = messageProducer.publishAndReceive(user.getId(), INTERNAL_EXCHANGE, INTERNAL_GET_NAME_PROFILE_ROUTING_KEY);
                userResponseList.add(new UserResponse(user.getId(), user.getUsername(), user.getRole(), name));
            }
            return ResponseEntity.ok(userResponseList);
        }
        return new ResponseEntity(invalidUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<UserResponse>> getAllUsersWithoutProject(String authorizationHeader, Long projectId) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            String check = messageProducer.publishAndReceive(projectId, INTERNAL_EXCHANGE, INTERNAL_PROJECT_EXISTS_ROUTING_KEY);
            if (check.equals(INVALID))
                return new ResponseEntity(serviceDoesNotExist(APPLICATION_USER, RETRIEVAL), HttpStatus.BAD_REQUEST);

            List<Long> userIds = messageProducer.publishAndReceiveList(projectId, INTERNAL_EXCHANGE, INTERNAL_GET_ALL_USERS_OF_PROJECT_ROUTING_KEY);
            List<ApplicationUser> applicationUsers = userRepository.findAllExceptUserIds(userIds);

            List<UserResponse> userResponseList = new ArrayList<>();
            for (ApplicationUser user : applicationUsers) {
                String name = messageProducer.publishAndReceive(user.getId(), INTERNAL_EXCHANGE, INTERNAL_GET_NAME_PROFILE_ROUTING_KEY);
                userResponseList.add(new UserResponse(user.getId(), user.getUsername(), user.getRole(), name));
            }
            return ResponseEntity.ok(userResponseList);
        }
        return new ResponseEntity(invalidUserFailed(APPLICATION_USER, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<String> removeUser(String authorizationHeader, Long id) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(APPLICATION_USER, DELETION), HttpStatus.BAD_REQUEST);

            Optional<ApplicationUser> user = userRepository.findById(id);
            if (user.isEmpty())
                return new ResponseEntity(serviceDoesNotExist(APPLICATION_USER, DELETION), HttpStatus.BAD_REQUEST);

            userRepository.deleteById(id);
            messageProducer.publish(id, INTERNAL_EXCHANGE, INTERNAL_REMOVE_PROFILE_ROUTING_KEY);
            messageProducer.publish(id, INTERNAL_EXCHANGE, INTERNAL_REMOVE_ALL_USER_PROJECTS_BY_USER_ROUTING_KEY);
            return ResponseEntity.ok(serviceSuccessful(APPLICATION_USER, DELETION));
        }
        return new ResponseEntity(invalidUserFailed(APPLICATION_USER, DELETION), HttpStatus.UNAUTHORIZED);
    }
}


package com.anillama.project;

import com.anillama.amqp.RabbitMQMessageProducer;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionRequest;
import com.anillama.clients.sessionmanagement.ApplicationUserSessionValidateService;
import com.anillama.clients.userproject.UserProjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.anillama.clients.application.ApplicationUtil.*;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ApplicationUserSessionValidateService sessionValidateService;
    private final RabbitMQMessageProducer messageProducer;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, ApplicationUserSessionValidateService sessionValidateService, RabbitMQMessageProducer messageProducer) {
        this.projectRepository = projectRepository;
        this.sessionValidateService = sessionValidateService;
        this.messageProducer = messageProducer;
    }

    public ResponseEntity<String> createProject(String authorizationHeader, ProjectRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(PROJECT, CREATION), HttpStatus.UNAUTHORIZED);
            Project project = projectRepository.getProjectByName(request.name());
            if (project != null)
                return new ResponseEntity(serviceAlreadyExists(PROJECT, CREATION), HttpStatus.BAD_REQUEST);
            project = Project.builder()
                    .name(request.name())
                    .description(request.description())
                    .createdDate(LocalDate.now())
                    .build();
            project = projectRepository.save(project);
            messageProducer.publish(new UserProjectRequest(response.userId(), project.getId()), INTERNAL_EXCHANGE, INTERNAL_REGISTER_USER_PROJECT_ROUTING_KEY);
            return ResponseEntity.ok(serviceSuccessful(PROJECT, CREATION));
        } else {
            return new ResponseEntity(invalidUserFailed(PROJECT, CREATION), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<Project> getProject(String authorizationHeader, Long id) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            String check = messageProducer.publishAndReceive(new UserProjectRequest(response.userId(), id), INTERNAL_EXCHANGE, INTERNAL_CHECK_USER_PROJECT_ROUTING_KEY);
            if (check.equals(INVALID))
                return new ResponseEntity(unauthorizedUserFailed(PROJECT, RETRIEVAL), HttpStatus.UNAUTHORIZED);
            Project project = projectRepository.getProjectById(id);
            if (project == null)
                return new ResponseEntity(serviceDoesNotExist(PROJECT, RETRIEVAL), HttpStatus.BAD_REQUEST);
            return ResponseEntity.ok(project);
        }
        return new ResponseEntity(invalidUserFailed(PROJECT, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<String> updateProject(String authorizationHeader, Long id, ProjectRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(PROJECT, UPDATE), HttpStatus.UNAUTHORIZED);
            Project savedProfile = projectRepository.getProjectById(id);
            if (savedProfile == null)
                return new ResponseEntity(serviceDoesNotExist(PROJECT, UPDATE), HttpStatus.BAD_REQUEST);
            savedProfile.setName(request.name());
            savedProfile.setDescription(request.description());
            projectRepository.save(savedProfile);
            return ResponseEntity.ok(serviceSuccessful(PROJECT, UPDATE));
        }
        return new ResponseEntity(invalidUserFailed(PROJECT, UPDATE), HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<String> deleteProject(String authorizationHeader, Long projectId) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(PROJECT, DELETION), HttpStatus.UNAUTHORIZED);
            Integer projectDeleted = projectRepository.deleteProjectById(projectId);
            if (projectDeleted.equals(0))
                return new ResponseEntity(serviceDoesNotExist(PROJECT, DELETION), HttpStatus.BAD_REQUEST);
            messageProducer.publish(projectId, INTERNAL_EXCHANGE, INTERNAL_REMOVE_ALL_USER_PROJECTS_BY_PROJECT_ROUTING_KEY);
            messageProducer.publish(projectId, INTERNAL_EXCHANGE, INTERNAL_REMOVE_ALL_TICKETS_BY_PROJECT_ROUTING_KEY);
            return ResponseEntity.ok(serviceSuccessful(PROJECT, DELETION));
        }
        return new ResponseEntity(invalidUserFailed(PROJECT, DELETION), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<Project>> getAllProjectsForUser(String authorizationHeader) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            List<Long> projectIds = messageProducer.publishAndReceiveList(response.userId(), INTERNAL_EXCHANGE, INTERNAL_GET_ALL_PROJECTS_FOR_USER_ROUTING_KEY);
            List<Project> projects = projectRepository.getProjectsByIds(projectIds);
            return ResponseEntity.ok(projects);
        }
        return new ResponseEntity(invalidUserFailed(PROJECT, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<List<Project>> getAllProjects(String authorizationHeader) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(PROJECT, RETRIEVAL), HttpStatus.UNAUTHORIZED);
            List<Project> projects = projectRepository.findAll();
            return ResponseEntity.ok(projects);
        }
        return new ResponseEntity(invalidUserFailed(PROJECT, RETRIEVAL), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<String> provideAccessToProject(String authorizationHeader, ProjectAccessRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(PROJECT, ACCESS), HttpStatus.UNAUTHORIZED);

            // TODO ==> Check user exists.
            Project project = projectRepository.getProjectById(request.projectId());
            if (project == null)
                return new ResponseEntity(serviceDoesNotExist(PROJECT, ACCESS), HttpStatus.BAD_REQUEST);
            messageProducer.publish(new UserProjectRequest(request.userId(), request.projectId()), INTERNAL_EXCHANGE, INTERNAL_REGISTER_USER_PROJECT_ROUTING_KEY);
            return ResponseEntity.ok(serviceSuccessful(PROJECT, ACCESS));
        } else {
            return new ResponseEntity(invalidUserFailed(PROJECT, ACCESS), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> revokeAccessToProject(String authorizationHeader, ProjectAccessRequest request) {
        ApplicationUserSessionRequest response = sessionValidateService.validateUser(authorizationHeader);
        if (response.token().equals(VALID)) {
            if (!response.role().equals(ADMIN))
                return new ResponseEntity(unauthorizedUserFailed(PROJECT, REVOKE), HttpStatus.UNAUTHORIZED);

            Project project = projectRepository.getProjectById(request.projectId());
            if (project == null)
                return new ResponseEntity(serviceDoesNotExist(PROJECT, REVOKE), HttpStatus.BAD_REQUEST);
            messageProducer.publish(new UserProjectRequest(request.userId(), request.projectId()), INTERNAL_EXCHANGE, INTERNAL_REMOVE_USER_PROJECT_ROUTING_KEY);
            return ResponseEntity.ok(serviceSuccessful(PROJECT, REVOKE));
        } else {
            return new ResponseEntity(invalidUserFailed(PROJECT, REVOKE), HttpStatus.UNAUTHORIZED);
        }
    }

    public String projectExists(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        if (project == null) return INVALID;
        return VALID;
    }
}

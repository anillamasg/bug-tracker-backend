package com.anillama.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.anillama.clients.application.ApplicationUtil.AUTHORIZATION;

@RestController
@Slf4j
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<String> createProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody ProjectRequest request) {
        log.info("createProject => {}", request);
        return projectService.createProject(authorizationHeader, request);
    }

    @PostMapping(value = "/provideAccess")
    public ResponseEntity<String> provideAccessToProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody ProjectAccessRequest request) {
        log.info("provideAccessToProject => {}", request);
        return projectService.provideAccessToProject(authorizationHeader, request);
    }

    @PostMapping(value = "/revokeAccess")
    public ResponseEntity<String> revokeAccessToProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @RequestBody ProjectAccessRequest request) {
        log.info("provideAccessToProject => {}", request);
        return projectService.revokeAccessToProject(authorizationHeader, request);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Project> getProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id) {
        log.info("getProject => {}", id);
        return projectService.getProject(authorizationHeader, id);
    }

    @GetMapping(value = "/allForUser")
    public ResponseEntity<List<Project>> getAllProjectsForUser(@RequestHeader(name = AUTHORIZATION) String authorizationHeader) {
        log.info("getAllProjectsForUser => {}");
        return projectService.getAllProjectsForUser(authorizationHeader);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Project>> getAllProjects(@RequestHeader(name = AUTHORIZATION) String authorizationHeader) {
        log.info("getAllProject => {}");
        return projectService.getAllProjects(authorizationHeader);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long id, @RequestBody ProjectRequest request) {
        log.info("updateProject => {}", request);
        return projectService.updateProject(authorizationHeader, id, request);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<String> deleteProject(@RequestHeader(name = AUTHORIZATION) String authorizationHeader, @PathVariable Long projectId) {
        log.info("deleteProject => {}", projectId);
        return projectService.deleteProject(authorizationHeader, projectId);
    }
}

package com.anillama.userproject;

import com.anillama.clients.userproject.UserProjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.anillama.clients.application.ApplicationUtil.INVALID;
import static com.anillama.clients.application.ApplicationUtil.VALID;

@Service
public class UserProjectService {
    private final UserProjectRepository userRepository;

    @Autowired
    public UserProjectService(UserProjectRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUserProject(UserProjectRequest request) {
        UserProject userProject = userRepository.findUserProjectByUserIdAndProjectId(request.userId(), request.projectId());
        if (userProject != null) return;
        userProject = UserProject.builder()
                .userId(request.userId())
                .projectId(request.projectId())
                .build();
        userRepository.save(userProject);
    }

    public String accessCheck(UserProjectRequest request) {
        UserProject userProject = userRepository.findUserProjectByUserIdAndProjectId(request.userId(), request.projectId());
        if (userProject == null) return INVALID;
        return VALID;
    }

    @Transactional
    public void removeAllUserProjectsByProject(Long projectId) {
        userRepository.deleteAllByProjectId(projectId);
    }

    @Transactional
    public void removeAllUserProjectsByUser(Long userId) {
        userRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public void removeUserProject(UserProjectRequest request) {
        userRepository.deleteUserProjectByUserIdAndProjectId(request.userId(), request.projectId());
    }

    public List<Long> getAllProjectsForUser(Long userId) {
        List<Long> projects = userRepository.findAllProjectsForUser(userId);
        return projects;
    }

    public List<Long> getAllUsersOfProject(Long projectId) {
        List<Long> users = userRepository.findAllUsersOfProject(projectId);
        return users;
    }
}

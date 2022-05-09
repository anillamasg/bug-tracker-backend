package com.anillama.userproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    UserProject findUserProjectByUserIdAndProjectId(Long userId, Long projectId);

    void deleteUserProjectByUserIdAndProjectId(Long userId, Long projectId);

    void deleteAllByProjectId(Long projectId);

    void deleteAllByUserId(Long userId);

    @Query("select up.projectId from UserProject up where up.userId=?1")
    List<Long> findAllProjectsForUser(Long userId);

    @Query("select up.userId from UserProject up where up.projectId=?1")
    List<Long> findAllUsersOfProject(Long projectId);
}

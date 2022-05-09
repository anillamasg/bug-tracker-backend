package com.anillama.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project getProjectById(Long id);

    Project getProjectByName(String name);

    Integer deleteProjectById(Long id);

    @Query("select p from Project p where p.id in (?1)")
    List<Project> getProjectsByIds(List<Long> ids);
}

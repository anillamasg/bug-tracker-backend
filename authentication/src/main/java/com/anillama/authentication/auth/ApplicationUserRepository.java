package com.anillama.authentication.auth;

import com.anillama.authentication.auth.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByUsername(String username);

    List<ApplicationUser> findAll();

    void deleteById(Long userId);

    @Query("select u from ApplicationUser u where u.id in (?1)")
    List<ApplicationUser> findAllByUserIds(List<Long> userIds);

    @Query("select u from ApplicationUser u where u.id not in (?1)")
    List<ApplicationUser> findAllExceptUserIds(List<Long> userIds);
}

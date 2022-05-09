package com.anillama.profile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile getProfileByUserId(Long userId);

    Integer deleteProfileByUserId(Long userId);
}

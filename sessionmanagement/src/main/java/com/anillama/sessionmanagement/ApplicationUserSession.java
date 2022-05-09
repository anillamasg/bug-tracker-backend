package com.anillama.sessionmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@RedisHash("UserSession")
public class ApplicationUserSession implements Serializable {
    @Id
    @Column(nullable = false, unique = true)
    private Long userId;
    private String token;
}

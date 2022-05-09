package com.anillama.authentication.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UsernamePasswordAuthenticationRequest {
    private String username;
    private String password;
}

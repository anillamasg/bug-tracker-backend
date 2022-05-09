package com.anillama.authentication.auth;

public record UserRequest(Long id, String email, String password, String role) {
}

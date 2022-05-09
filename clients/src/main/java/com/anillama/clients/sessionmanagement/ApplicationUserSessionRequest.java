package com.anillama.clients.sessionmanagement;

public record ApplicationUserSessionRequest (Long userId, String token, String role) {
}

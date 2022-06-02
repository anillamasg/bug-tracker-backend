package com.anillama.clients.profile;

public record ProfileRequest(Long userId, String name, String email) {
}

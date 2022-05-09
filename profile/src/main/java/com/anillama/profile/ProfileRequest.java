package com.anillama.profile;

public record ProfileRequest(Long userId, String name, String email) {
}

package com.moirai.alloc.auth.sync.dto;

public record AuthUserSyncRequest(
        Long userId,
        String loginId,
        String email,
        String password,
        String role,
        String status
) {
}

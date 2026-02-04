package com.moirai.alloc.auth.sync.controller;

import com.moirai.alloc.auth.sync.dto.AuthUserSyncRequest;
import com.moirai.alloc.auth.sync.service.AuthUserSyncService;
import com.moirai.alloc.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/users")
public class AuthUserSyncController {

    private final AuthUserSyncService service;

    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('INTERNAL')")
    public ApiResponse<Void> sync(@RequestBody AuthUserSyncRequest request) {
        service.sync(request);
        return ApiResponse.success(null);
    }
}

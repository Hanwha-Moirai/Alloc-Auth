package com.moirai.alloc.auth.sync.service;

import com.moirai.alloc.auth.sync.dto.AuthUserSyncRequest;
import com.moirai.alloc.user.command.domain.User;
import com.moirai.alloc.user.command.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUserSyncService {

    private final UserRepository userRepository;

    @Transactional
    public void sync(AuthUserSyncRequest req) {
        User user = userRepository.findById(req.userId()).orElse(null);

        User.Auth auth = User.Auth.valueOf(req.role());
        User.Status status = User.Status.valueOf(req.status());

        if (user == null) {
            userRepository.save(
                    User.builder()
                            .userId(req.userId())
                            .loginId(req.loginId())
                            .email(req.email())
                            .password(req.password())
                            .auth(auth)
                            .status(status)
                            .build()
            );
            return;
        }

        user.changeLoginId(req.loginId());
        user.changeEmail(req.email());
        if (req.password() != null && !req.password().isBlank()) {
            user.changePassword(req.password());
        }
        user.changeAuth(auth);
        user.changeStatus(status);
    }
}

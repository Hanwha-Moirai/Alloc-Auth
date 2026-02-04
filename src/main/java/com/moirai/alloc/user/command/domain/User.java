package com.moirai.alloc.user.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_users_login_id", columnNames = "login_id"),
                @UniqueConstraint(name = "UK_users_email", columnNames = "email")
        }
)
public class User {

    public enum Status { ACTIVE, SUSPENDED, DELETED }
    public enum Auth { ADMIN, USER, PM }

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", length = 100, nullable = false)
    private String loginId;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth", nullable = false)
    private Auth auth;

    @Builder
    private User(Long userId,
                 String loginId,
                 String password,
                 String email,
                 Auth auth,
                 Status status) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.auth = (auth == null) ? Auth.USER : auth;
        this.status = (status == null) ? Status.ACTIVE : status;
    }

    public void changePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
        this.password = encodedPassword;
    }

    public void changeLoginId(String loginId) {
        if (loginId != null && !loginId.isBlank()) {
            this.loginId = loginId;
        }
    }

    public void changeEmail(String email) {
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
    }

    public void changeAuth(Auth auth) {
        if (auth != null) this.auth = auth;
    }

    public void changeStatus(Status status) {
        if (status != null) this.status = status;
    }
}

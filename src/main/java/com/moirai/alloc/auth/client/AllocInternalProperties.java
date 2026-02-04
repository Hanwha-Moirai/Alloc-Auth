package com.moirai.alloc.auth.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "alloc.internal")
public class AllocInternalProperties {
    private String baseUrl;
    private String passwordResetPath = "/api/internal/users/password/reset";
}

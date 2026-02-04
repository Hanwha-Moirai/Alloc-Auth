package com.moirai.alloc.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.moirai.alloc")
@EnableJpaRepositories(basePackages = "com.moirai.alloc.user.command.repository")
@EntityScan(basePackages = "com.moirai.alloc.user.command.domain")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}

package com.moirai.alloc.auth.client;

import com.moirai.alloc.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AllocInternalPasswordResetClient {

    private final AllocInternalProperties props;
    private final JwtTokenProvider jwtTokenProvider;

    @Qualifier("allocInternalWebClient")
    private final WebClient webClient;

    @Value("${spring.application.name:auth-service}")
    private String internalSubject;

    public void resetPassword(String email, String newPassword) {
        String internalJwt = jwtTokenProvider.createInternalToken(internalSubject);

        webClient.post()
                .uri(props.getPasswordResetPath())
                .headers(h -> h.setBearerAuth(internalJwt))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new PasswordResetRequest(email, newPassword))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(body -> new IllegalStateException(
                                        "Alloc password reset failed. status=" + response.statusCode() + " body=" + body
                                ))
                )
                .bodyToMono(Void.class)
                .block();
    }

    private record PasswordResetRequest(String email, String newPassword) {}
}

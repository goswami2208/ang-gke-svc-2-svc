package com.example.appa;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Configuration
public class AppBClientConfig {

    @Value("${app-b.base-url}")
    private String appBBaseUrl;

    @Bean
    public RestClient appBRestClient() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        if (!(credentials instanceof IdTokenProvider idTokenProvider)) {
            throw new IllegalStateException("Application Default Credentials do not support ID tokens.");
        }

        // Automatically caches and refreshes the Google-signed JWT for targetAudience
        IdTokenCredentials idTokenCredentials = IdTokenCredentials.newBuilder()
            .setIdTokenProvider(idTokenProvider)
            .setTargetAudience(appBBaseUrl)
            .setOptions(List.of(IdTokenProvider.Option.INCLUDE_EMAIL))
            .build();

        return RestClient.builder()
            .baseUrl(appBBaseUrl)
            .requestInterceptor((request, body, execution) -> {
                idTokenCredentials.refreshIfExpired();
                String tokenValue = idTokenCredentials.getIdToken().getTokenValue();
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
                return execution.execute(request, body);
            })
            .build();
    }
}
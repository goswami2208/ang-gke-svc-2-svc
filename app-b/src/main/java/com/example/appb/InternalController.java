package com.example.appb;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/secret-data")
    public Map<String, Object> getSecretData(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "service", "app-b",
                "status", "verified-oidc",
                "callerIdentity", jwt.getClaimAsString("email"),
                "timestamp", Instant.now().toString());
    }
}
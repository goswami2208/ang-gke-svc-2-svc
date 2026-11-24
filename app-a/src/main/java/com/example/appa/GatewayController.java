package com.example.appa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private final RestClient appBRestClient;

    public GatewayController(RestClient appBRestClient) {
        this.appBRestClient = appBRestClient;
    }

    @GetMapping("/aggregate")
    public Map<String, Object> getAggregatedData() {
        Map<?, ?> appBResponse = appBRestClient.get()
            .uri("/internal/secret-data")
            .retrieve()
            .body(Map.class);

        return Map.of(
            "gateway", "app-a",
            "downstream", appBResponse
        );
    }
}
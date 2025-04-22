package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Balance;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BalanceQuerier {
    private final RestTemplate restTemplate;
    private String baseUrl;

    public BalanceQuerier(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        // Default fallback URL (can be overridden in tests)
        this.baseUrl = "http://localhost:33400";
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Balance query(Long userId) {
        String url = baseUrl + "/balance?userId=" + userId;
        return restTemplate.getForObject(url, Balance.class);
    }
}

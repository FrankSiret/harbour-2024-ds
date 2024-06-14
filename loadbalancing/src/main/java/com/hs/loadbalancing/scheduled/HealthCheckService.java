package com.hs.loadbalancing.scheduled;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import com.hs.loadbalancing.configuration.ApplicationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class HealthCheckService {

    private final RestTemplate restTemplate;

    private final ConcurrentHashMap<String, Boolean> serviceStatus = new ConcurrentHashMap<>();

    private final List<String> serviceUrls;

    HealthCheckService(RestTemplate restTemplate, ApplicationProperties properties) {
        this.restTemplate = restTemplate;
        this.serviceUrls = properties.getServices().getInstances();
    }

    @Scheduled(fixedRateString = "${application.health.check-interval}")
    public void checkServices() {
        log.info("Starting scheduled : {}", serviceUrls);
        serviceUrls.forEach(url -> {
            try {
                String healthUrl = url + "/actuator/health";
                HealthStatus response = restTemplate.getForObject(healthUrl, HealthStatus.class);
                log.debug("Scheduled response {}", response);
                serviceStatus.put(url, "UP".equals(response.getStatus()));
            } catch (Exception e) {
                log.warn("Scheduled response error {}", e.getMessage());
                serviceStatus.put(url, false);
            }
        });
    }

    @Data
    @ToString
    public static class HealthStatus {
        String status; 
    }

    public List<String> getHealthyServices() {
        List<String> healthyServices = new ArrayList<>();
        serviceStatus.forEach((url, isHealthy) -> {
            if (Boolean.TRUE.equals(isHealthy)) {
                healthyServices.add(url);
            }
        });
        return healthyServices;
    }

    public void register(String host, String port) {
        if(!host.startsWith("http")) {
            host = "http://" + host;
        }
        String url = host + ":" + port;
        if (!serviceUrls.contains(url)) {
            serviceUrls.add(url);
        }
    }
}

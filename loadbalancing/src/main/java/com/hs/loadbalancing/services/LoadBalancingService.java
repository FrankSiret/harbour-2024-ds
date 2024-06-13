package com.hs.loadbalancing.services;

import org.springframework.stereotype.Service;

import com.hs.loadbalancing.scheduled.HealthCheckService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadBalancingService {

    private final HealthCheckService healthCheckService;

    private final AtomicInteger counter = new AtomicInteger(0);

    LoadBalancingService(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    public String getNextServiceUrl() {
        List<String> healthyServices = healthCheckService.getHealthyServices();
        if (healthyServices.isEmpty()) {
            throw new RuntimeException("No healthy services available");
        }
        int index = counter.getAndIncrement() % healthyServices.size();
        return healthyServices.get(index);
    }

    public List<String> getValidRoute() {
        return healthCheckService.getHealthyServices();
    }

    public void register(String host, String port) {
        healthCheckService.register(host, port);
    }
}

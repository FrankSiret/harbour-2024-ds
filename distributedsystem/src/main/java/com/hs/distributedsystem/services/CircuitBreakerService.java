package com.hs.distributedsystem.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hs.distributedsystem.domain.Transaction;

import java.util.concurrent.CompletableFuture;

@Service
public class CircuitBreakerService {

    private final RestTemplate restTemplate;

    CircuitBreakerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "my-circuit-breaker", fallbackMethod = "fallback")
    @Retry(name = "my-circuit-breaker")
    @TimeLimiter(name = "my-circuit-breaker")
    public CompletableFuture<ResponseEntity<Void>> callService(String url, HttpEntity<Transaction> request) {
        return CompletableFuture.supplyAsync(
                () -> restTemplate.postForEntity(url, request, Void.class));
    }

    public CompletableFuture<ResponseEntity<String>> fallback(String url, HttpEntity<Transaction> request, Throwable throwable) {
        return CompletableFuture
                .completedFuture(ResponseEntity.status(503).body("Transaction service is taking too long to respond"));
    }
}

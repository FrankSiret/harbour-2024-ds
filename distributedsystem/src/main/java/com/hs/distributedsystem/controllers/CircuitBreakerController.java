package com.hs.distributedsystem.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hs.distributedsystem.domain.Transaction;
import com.hs.distributedsystem.services.CircuitBreakerService;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api")
public class CircuitBreakerController {

    private final CircuitBreakerService circuitBreakerService;

    CircuitBreakerController(CircuitBreakerService circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @PostMapping("/transactions/cb")
    public CompletableFuture<ResponseEntity<Void>> createTransactionWithCircuitBreaker(
            @RequestHeader(value = "X-requested-latency", required = false) Long latency,
            @RequestBody Transaction transactionDTO) {
        log.info("REST request to create transaction with circuit breaker : {}, latency : {}", transactionDTO, latency);

        HttpHeaders headers = new HttpHeaders();
        if (latency != null) {
            headers.add("X-requested-latency", latency.toString());
        }
        HttpEntity<Transaction> request = new HttpEntity<>(transactionDTO, headers);

        String redirect = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                     .path("/api/transactions")
                                                     .toUriString();

        log.debug("Redirect cb : {}", redirect);

        return circuitBreakerService.callService(redirect, request);
    }

}

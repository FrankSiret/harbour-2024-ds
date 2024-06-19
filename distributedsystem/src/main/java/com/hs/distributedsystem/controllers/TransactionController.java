package com.hs.distributedsystem.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hs.distributedsystem.domain.Transaction;
import com.hs.distributedsystem.services.TransactionService;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<Void> createTransaction(
        @RequestHeader(value = "X-requested-latency", required = false) Long latency,
        @RequestBody Transaction transaction
    ) throws Exception {
        log.info("REST request to create transaction : {}, latency : {}", transaction, latency);

        if(latency != null) {
            Thread.sleep(latency);
        }

        transactionService.createTransaction(transaction, latency);

        return ResponseEntity.created(URI.create("/api/transactions/" + transaction.getTransactionId())).build();
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<Transaction> getByTransactionId(@PathVariable String transactionId) throws SQLException {
        log.info("REST request to get transactions by transactionId : {}", transactionId);

        Optional<Transaction> result = transactionService.getTransactionById(transactionId);

        if(result.isPresent()) {
            return ResponseEntity.ok().body(result.get());
        }

        return ResponseEntity.notFound().build();
    }
    
}

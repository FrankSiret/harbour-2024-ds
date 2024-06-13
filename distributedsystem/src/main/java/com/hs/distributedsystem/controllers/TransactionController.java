package com.hs.distributedsystem.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hs.distributedsystem.services.TransactionService;
import com.hs.distributedsystem.services.dto.TransactionDTO;
import com.hs.distributedsystem.services.dto.TransactionResponseDTO;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
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
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) throws Exception {
        log.info("REST request to create transaction : {}", transactionDTO);

        TransactionResponseDTO result = transactionService.createTransaction(transactionDTO);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        log.info("REST request to get all transactions");

        List<TransactionResponseDTO> result = transactionService.findAll();

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getByTransactionId(@PathVariable String transactionId) {
        log.info("REST request to get transactions by transactionId : {}", transactionId);

        Optional<TransactionResponseDTO> result = transactionService.findByTransactionId(transactionId);

        if(result.isPresent()) {
            return ResponseEntity.ok().body(result.get());
        }

        return ResponseEntity.notFound().build();
    }
    
}

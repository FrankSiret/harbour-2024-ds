package com.hs.distributedsystem.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hs.distributedsystem.services.OutboxService;
import com.hs.distributedsystem.services.TransactionService;
import com.hs.distributedsystem.services.dto.OutboxDTO;
import com.hs.distributedsystem.services.dto.TransactionDTO;
import com.hs.distributedsystem.services.dto.TransactionResponseDTO;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    private final RabbitTemplate rabbitTemplate;

    private final OutboxService outboxService;

    TransactionController(
        TransactionService transactionService,
        RabbitTemplate rabbitTemplate,
        OutboxService outboxService
    ) {
        this.transactionService = transactionService;
        this.rabbitTemplate = rabbitTemplate;
        this.outboxService = outboxService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<OutboxDTO> createTransaction(
        @RequestHeader(value = "X-requested-latency", required = false) Long latency,
        @RequestBody TransactionDTO transactionDTO
    ) throws Exception {
        log.info("REST request to create transaction : {}, latency : {}", transactionDTO, latency);

        OutboxDTO outboxDTO = new OutboxDTO();
        outboxDTO.setId(UUID.randomUUID());
        outboxDTO.setStatus("PENDING");
        outboxDTO.setMessage(transactionDTO);
        outboxDTO.setLatency(latency);
        outboxDTO.setCreatedAt(Instant.now());
        outboxDTO = outboxService.save(outboxDTO);

        sendMessage(outboxDTO);

        return ResponseEntity.ok().body(outboxDTO);
    }

    public void sendMessage(OutboxDTO outboxDTO) {
        rabbitTemplate.convertAndSend("my-exchange", "my-key", outboxDTO);
    }

    @GetMapping("/transactions/rmq/{uuid}")
    public ResponseEntity<OutboxDTO> getRmqTransaction(@PathVariable UUID uuid) {
        log.info("REST request to get rabbit mq transaction : {}", uuid);

        Optional<OutboxDTO> result = outboxService.findById(uuid);

        if(result.isPresent()) {
            return ResponseEntity.ok().body(result.get());
        }
        return ResponseEntity.notFound().build();
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

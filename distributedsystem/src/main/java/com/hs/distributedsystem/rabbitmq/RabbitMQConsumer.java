package com.hs.distributedsystem.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hs.distributedsystem.services.OutboxService;
import com.hs.distributedsystem.services.TransactionService;
import com.hs.distributedsystem.services.dto.OutboxDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RabbitMQConsumer {

    private final TransactionService transactionService;

    private final OutboxService outboxService;

    RabbitMQConsumer(TransactionService transactionService, OutboxService outboxService) {
        this.transactionService = transactionService;
        this.outboxService = outboxService;
    }
  
    @RabbitListener(queues = "my-queue")
    public void handleMessage(OutboxDTO outboxDTO) throws Exception {
        log.info("Received message: {}", outboxDTO);

        outboxDTO.setStatus("RUNNING");
        outboxService.save(outboxDTO);
        
        if(outboxDTO.getLatency() != null) {
            Thread.sleep(outboxDTO.getLatency());
        }
        
        transactionService.createTransaction(outboxDTO.getMessage(), outboxDTO.getLatency());
        
        outboxDTO.setStatus("COMPLETED");
        outboxService.save(outboxDTO);
    }

}

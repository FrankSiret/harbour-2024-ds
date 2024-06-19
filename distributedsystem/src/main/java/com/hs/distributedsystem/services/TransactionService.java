package com.hs.distributedsystem.services;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.hs.distributedsystem.controllers.exception.CustomException;
import com.hs.distributedsystem.controllers.vm.GenericResponseVM;
import com.hs.distributedsystem.domain.Transaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class TransactionService {

    @Value("${harbour-cloud-computing.url}")
    private String harbourCloudComputingUrl;

    private final RestTemplate restTemplate;

    private final TransactionRepository transactionRepository;

    TransactionService(RestTemplate restTemplate, TransactionRepository transactionRepository) {
        this.restTemplate = restTemplate;
        this.transactionRepository = transactionRepository;
    }

    public void saveTransaction(Transaction transaction) throws SQLException {
        log.info("Request to save transaction : {}", transaction);
        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(String transactionId) throws SQLException {
        log.info("Request to get transactionResponse by transactionId : {}", transactionId);
        return transactionRepository.findById(transactionId);
    }

    public void createTransaction(Transaction transactionDTO, Long latency) throws Exception {
        log.info("Request to create transaction : {}", transactionDTO);

        HttpHeaders headers = new HttpHeaders();
        if (latency != null) {
            headers.add("X-requested-latency", latency.toString());
        }
        HttpEntity<Transaction> request = new HttpEntity<>(transactionDTO, headers);

        GenericResponseVM response = restTemplate.postForObject(harbourCloudComputingUrl, request,
                GenericResponseVM.class);

        if (response == null) {
            throw new Exception("Response null");
        }
        if (response.getError() != null) {
            throw new CustomException(response.getError(), HttpStatus.BAD_REQUEST);
        }
        if (response.getData() == null) {
            throw new CustomException("Data is null", HttpStatus.BAD_REQUEST);
        }

        Transaction result = new Transaction();
        result.setTransactionId(response.getData().getTransactionId());
        result.setStatus(response.getData().getStatus());
        result.setAmount(transactionDTO.getAmount());
        result.setCurrency(transactionDTO.getCurrency());
        result.setDescription(transactionDTO.getDescription());
        result.setUserId(transactionDTO.getUserId());

        saveTransaction(result);
    }

}

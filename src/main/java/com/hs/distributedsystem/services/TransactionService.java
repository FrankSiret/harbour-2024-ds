package com.hs.distributedsystem.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.hs.distributedsystem.controllers.exception.CustomException;
import com.hs.distributedsystem.controllers.vm.GenericResponseVM;
import com.hs.distributedsystem.domain.Transaction;
import com.hs.distributedsystem.domain.TransactionResponse;
import com.hs.distributedsystem.repository.TransactionRepository;
import com.hs.distributedsystem.repository.TransactionResponseRepository;
import com.hs.distributedsystem.services.dto.TransactionDTO;
import com.hs.distributedsystem.services.dto.TransactionResponseDTO;
import com.hs.distributedsystem.services.mapper.TransactionMapper;
import com.hs.distributedsystem.services.mapper.TransactionResponseMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class TransactionService {

    @Value("${harbour-cloud-computing.url}")
    private String harbourCloudComputingUrl;

    private final RestTemplate restTemplate;

    private final TransactionMapper transactionMapper;

    private final TransactionRepository transactionRepository;

    private final TransactionResponseMapper transactionResponseMapper;

    private final TransactionResponseRepository transactionResponseRepository;

    TransactionService(
            RestTemplate restTemplate,
            TransactionMapper transactionMapper,
            TransactionRepository transactionRepository,
            TransactionResponseMapper transactionResponseMapper,
            TransactionResponseRepository transactionResponseRepository) {
        this.restTemplate = restTemplate;
        this.transactionMapper = transactionMapper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionRepository = transactionRepository;
        this.transactionResponseRepository = transactionResponseRepository;
    }

    public TransactionDTO save(TransactionDTO transactionDTO) {
        log.info("Request to save transaction : {}", transactionDTO);
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    public TransactionResponseDTO save(TransactionResponseDTO transactionResponseDTO) {
        log.info("Request to save transactionResponse : {}", transactionResponseDTO);
        TransactionResponse transactionResponse = transactionResponseMapper.toEntity(transactionResponseDTO);
        transactionResponse = transactionResponseRepository.save(transactionResponse);
        return transactionResponseMapper.toDto(transactionResponse);
    }

    public TransactionResponseDTO createTransaction(TransactionDTO transactionDTO) throws Exception {
        log.info("Request to create transaction : {}", transactionDTO);

        GenericResponseVM response = restTemplate.postForObject(harbourCloudComputingUrl, transactionDTO,
                GenericResponseVM.class);

        if(response == null) {
            throw new Exception("Response null");
        }
        if(response.getError() != null) {
            throw new CustomException(response.getError(), HttpStatus.BAD_REQUEST);
        }
        if(response.getData() == null) {
            throw new CustomException("Data is null", HttpStatus.BAD_REQUEST);
        }

        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setTransactionId(response.getData().getTransactionId());
        transactionResponseDTO.setStatus(response.getData().getStatus());
        transactionResponseDTO.setDetail(transactionDTO);

        return save(transactionResponseDTO);
    }

    public List<TransactionResponseDTO> findAll() {
        log.info("Request to get all transactionResponse");
        return transactionResponseMapper.toDto(transactionResponseRepository.findAll());
    }

    public Optional<TransactionResponseDTO> findByTransactionId(String transactionId) {
        log.info("Request to get transactionResponse by transactionId : {}", transactionId);
        return transactionResponseRepository.findByTransactionId(transactionId).map(transactionResponseMapper::toDto);
    }
}

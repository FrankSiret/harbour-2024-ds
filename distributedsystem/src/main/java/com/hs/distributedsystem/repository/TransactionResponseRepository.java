package com.hs.distributedsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hs.distributedsystem.domain.TransactionResponse;

@Repository
public interface TransactionResponseRepository extends JpaRepository<TransactionResponse, Long> {

  Optional<TransactionResponse> findByTransactionId(String transactionId);

}

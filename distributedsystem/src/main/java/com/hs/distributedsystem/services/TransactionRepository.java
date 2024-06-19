package com.hs.distributedsystem.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hs.distributedsystem.domain.Transaction;

@Service
public class TransactionRepository {

  private final ShardsConnectionService shardsConnectionService;

  TransactionRepository(ShardsConnectionService shardsConnectionService) {
    this.shardsConnectionService = shardsConnectionService;
  }

  public void save(Transaction transaction) throws SQLException {
    String insertSQL = "INSERT INTO transaction (transaction_id, status, amount, currency, description, user_id) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection connection = shardsConnectionService.getShardConnection(transaction.getTransactionId());
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
      preparedStatement.setString(1, transaction.getTransactionId());
      preparedStatement.setString(2, transaction.getStatus());
      preparedStatement.setBigDecimal(3, transaction.getAmount());
      preparedStatement.setString(4, transaction.getCurrency());
      preparedStatement.setString(5, transaction.getDescription());
      preparedStatement.setString(6, transaction.getUserId());
      preparedStatement.executeUpdate();
    }
    shardsConnectionService.closeConnection();
  }

  public Optional<Transaction> findById(String transactionId) throws SQLException {
    String selectSQL = "SELECT transaction_id, status, amount, currency, description, user_id FROM transaction where transaction_id = ?";
    Transaction transaction = null;
    try (Connection connection = shardsConnectionService.getShardConnection(transactionId);
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
      preparedStatement.setString(1, transactionId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String status = resultSet.getString("status");
          BigDecimal amount = resultSet.getBigDecimal("amount");
          String currency = resultSet.getString("currency");
          String description = resultSet.getString("description");
          String userId = resultSet.getString("user_id");
          transaction = new Transaction();
          transaction.setTransactionId(transactionId);
          transaction.setStatus(status);
          transaction.setAmount(amount);
          transaction.setCurrency(currency);
          transaction.setDescription(description);
          transaction.setUserId(userId);
        }
      }
    }
    shardsConnectionService.closeConnection();
    return Optional.ofNullable(transaction);
  }

}

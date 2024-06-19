package com.hs.distributedsystem.domain;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Transaction {

    private String transactionId;

    private String status;

    private BigDecimal amount;

    private String currency;

    private String description;

    private String userId;

}

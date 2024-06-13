package com.hs.distributedsystem.services.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionDTO {

	private BigDecimal amount;

	private String currency;

	private String description;

	private String userId;

}

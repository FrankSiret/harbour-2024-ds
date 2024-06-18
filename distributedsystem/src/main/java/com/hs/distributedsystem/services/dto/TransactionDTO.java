package com.hs.distributedsystem.services.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class TransactionDTO {

	private Long id;

	private BigDecimal amount;

	private String currency;

	private String description;

	private String userId;

}

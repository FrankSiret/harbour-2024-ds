package com.hs.distributedsystem.services.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionDTO implements Serializable {

	private static final long serialVersionUID = 68287784590L;

	private BigDecimal amount;

	private String currency;

	private String description;

	private String userId;

}

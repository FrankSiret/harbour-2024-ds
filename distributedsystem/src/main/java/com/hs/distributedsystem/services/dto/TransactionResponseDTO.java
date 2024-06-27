package com.hs.distributedsystem.services.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionResponseDTO implements Serializable {

	private static final long serialVersionUID = 98517921520L;

	private String transactionId;

	private String status;

	private TransactionDTO detail;

}

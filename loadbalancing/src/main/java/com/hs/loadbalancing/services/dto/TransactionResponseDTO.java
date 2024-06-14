package com.hs.loadbalancing.services.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionResponseDTO {

	private String transactionId;

	private String status;

	private TransactionDTO detail;

}

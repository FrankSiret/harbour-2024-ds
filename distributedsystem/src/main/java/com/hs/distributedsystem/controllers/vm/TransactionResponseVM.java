package com.hs.distributedsystem.controllers.vm;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionResponseVM {

	private String transactionId;

	private String status;

}

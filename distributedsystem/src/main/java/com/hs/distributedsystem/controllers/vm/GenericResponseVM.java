package com.hs.distributedsystem.controllers.vm;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GenericResponseVM {

	private TransactionResponseVM data;

	private String error;
}

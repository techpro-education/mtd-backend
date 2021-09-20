package com.bank.request;

 import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TransactionRequest {
	
	@NotBlank 
	private double amount;
	
	private String comment;

}

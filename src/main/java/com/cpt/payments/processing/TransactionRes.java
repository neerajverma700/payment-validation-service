package com.cpt.payments.processing;

import lombok.Data;

@Data
public class TransactionRes {

	private String txnReference;
	private String txnStatus;
	
	private String redirectUrl;

}

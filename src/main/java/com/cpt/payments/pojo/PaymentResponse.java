package com.cpt.payments.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentResponse {

	private String txnId;
	private String redirectUrl;
	
	public PaymentResponse(String txnId, String redirectUrl) {
		this.txnId = txnId;
		this.redirectUrl = redirectUrl;
	}
}

package com.cpt.payments.processing;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessingPaymentError {
	
	private String errorCode;
	private String errorMessage;
	
	public ProcessingPaymentError(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
